package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.github.felipegutierrez.elevatorsystem.actors.exceptions.BuildingCoordinatorException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol._
import org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorPanelProtocol.PickUpRequestSuccess
import org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorProtocol.{ElevatorId, Floor, MakeMove, MoveRequest, RequestElevatorState}
import org.github.felipegutierrez.elevatorsystem.actors.util.BuildingUtil
import org.github.felipegutierrez.elevatorsystem.services.{ElevatorControlSystem, ElevatorControlSystemFCFS, ElevatorControlSystemScan}

import scala.collection.immutable.Queue
import scala.concurrent.Await
import scala.concurrent.duration._

object BuildingCoordinator {
  def props(actorName: String = "buildingCoordinatorActor",
            numberOfFloors: Int = 10,
            numberOfElevators: Int = 1,
            elevatorControlSystemType: ElevatorControlSystem.ElevatorControlSystemType = ElevatorControlSystem.FCFSControlSystem) = {

    if (numberOfElevators < 0 || numberOfElevators > 16) throw new BuildingCoordinatorException("Number of elevators must be between 1 and 16")
    if (numberOfFloors < 2) throw new BuildingCoordinatorException("This is not a building. It is a house")

    val elevatorControlSystem: ElevatorControlSystem = elevatorControlSystemType match {
      case elevatorControlSystemType if (elevatorControlSystemType == ElevatorControlSystem.FCFSControlSystem) => new ElevatorControlSystemFCFS(numberOfFloors, numberOfElevators)
      case elevatorControlSystemType if (elevatorControlSystemType == ElevatorControlSystem.ScanControlSystem) => new ElevatorControlSystemScan(numberOfFloors, numberOfElevators)
      case _ => throw new RuntimeException("Elevator system type unimplemented")
    }

    Props(new BuildingCoordinator(actorName, numberOfFloors, numberOfElevators, elevatorControlSystem))
  }
}

/**
 * The [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]] actor is created with a name,
 * a fixed number of floors, a fixed number of elevators, and a elevator control system which implements the
 * interface [[org.github.felipegutierrez.elevatorsystem.services.ElevatorControlSystem]]. The default elevator
 * control system is the [[org.github.felipegutierrez.elevatorsystem.services.ElevatorControlSystemFCFS]] which
 * implements the First-Come-First-Serve logic for PickUp requests.
 *
 * @param actorName
 * @param numberOfFloors
 * @param numberOfElevators
 * @param elevatorControlSystem
 */
case class BuildingCoordinator(actorName: String,
                               numberOfFloors: Int,
                               numberOfElevators: Int,
                               elevatorControlSystem: ElevatorControlSystem)
  extends Actor with ActorLogging {

  implicit val timeout = Timeout(10 seconds)

  val elevators = createElevators(numberOfElevators)

  /**
   * The default handler of the Building coordinator starts with the [[operational]] handler with empty lists of the stopsRequests and pickUpRequests.
   * New stopsRequests and pickUpRequests are added in a stateless manner using immutable state.
   *
   * @return
   */
  override def receive: Receive = operational(1: ElevatorId, Map[ElevatorId, Queue[Floor]](), Map[ElevatorId, Queue[Floor]]())

  /**
   * The handler that manages the stateless behavior of the Building Coordinator.
   * Every time that the [[Panel]] actor sends a [[PickUpRequest]] message the Building Coordinator handles these messages in a non-blocking manner.
   * Therefore, it simulates that anyone at any floor can request a pickUp.
   * The elevator will arrive at any time in the future, but the pickUp request is always non-blocking.
   * So, the Building coordinator actor sends a [[MoveElevator]] message with the [[currentElevatorId]] to self asking some elevator to move.
   * The actor changes its handler to receive [[PickUpRequest]] messages with a [[nextElevatorId]].
   * The [[nextElevatorId]] is computed using round-robin approach.
   *
   * The process within the [[MoveElevator]] message is essentially blocking because we cannot change the behavior of an elevator if it is moving.
   * In order to make an [[Elevator]] move the [[BuildingCoordinator]] actor has to send a sequence of messages:
   * [[RequestElevatorState]] -> [[MoveRequest]] -> [[MakeMove]] then the [[Elevator]] arrives on the floor.
   * If the [[PickUpRequest]] was made, the passenger enters in the Elevator and he/she may send a [[DropOffRequest]] that is generated randomly and it is send in a non-blocking manner to the [[Elevator]].
   * Thereby, a passenger inside an elevator may request the elevator to go to any floor.
   * Its request is attended not in the exactly time that it is issued, but according to the controller that the Building Coordinator is using.
   *
   * @param stopsRequests  a Map that contains the stops that one elevator must attend.
   * @param pickUpRequests a Map that contains the pickUps issued from the [[Panel]] actor.
   * @return
   */
  def operational(currentElevatorId: ElevatorId, stopsRequests: Map[ElevatorId, Queue[Floor]], pickUpRequests: Map[ElevatorId, Queue[Floor]]): Receive = {
    case request@PickUpRequest(pickUpFloor, direction) =>
      println(s"[BuildingCoordinator] received a $request from floor[$pickUpFloor] to go [$direction] and will find an elevator to send.")

      if (pickUpFloor > numberOfFloors || pickUpFloor < 0) throw new BuildingCoordinatorException(s"I cannot pick up you because the floor $pickUpFloor does not exist in this building")
      if (direction != Direction(+1) && direction != Direction(-1)) throw new BuildingCoordinatorException("the directions that this elevators supports are only: up [+1] and down [-1]")
      if (pickUpFloor == 0 && direction == Direction(-1)) throw new BuildingCoordinatorException("you cannot go down because you are on the ground floor.")
      if (pickUpFloor == numberOfFloors && direction == Direction(+1)) throw new BuildingCoordinatorException("you cannot go up because you are on the last floor.")

      // change the Elevators state and calling context.become
      val stopsRequestsElevator = stopsRequests.get(currentElevatorId).getOrElse(Queue[Floor]())
      val newStopsRequestsElevator = {
        if (!stopsRequestsElevator.contains(pickUpFloor)) stopsRequestsElevator.enqueue(pickUpFloor)
        else stopsRequestsElevator
      }
      val newStopsRequests = stopsRequests + (currentElevatorId -> newStopsRequestsElevator)

      val pickUpRequestsElevator = pickUpRequests.get(currentElevatorId).getOrElse(Queue[Floor]())
      val newPickUpRequestsElevator = {
        if (!pickUpRequestsElevator.contains(pickUpFloor)) pickUpRequestsElevator.enqueue(pickUpFloor)
        else pickUpRequestsElevator
      }
      val newPickUpRequests = pickUpRequests + (currentElevatorId -> newPickUpRequestsElevator)

      // the next elevator Id is get in a round robin fashion
      val nextElevatorId: ElevatorId = if (currentElevatorId + 1 > numberOfElevators) 1 else currentElevatorId + 1
      context.become(operational(nextElevatorId, newStopsRequests, newPickUpRequests))

      sender() ! PickUpRequestSuccess

      // the building coordinator moves the current elevator
      self ! MoveElevator(currentElevatorId, direction)

    case msg@MoveElevator(elevatorId, direction) =>
      println(s"[BuildingCoordinator] received $msg")
      // it is possible to have duplicate requests to the same floor and we don't have to call the elevator more than once
      if (stopsRequests.get(elevatorId).nonEmpty) {
        val elevatorActor: ActorSelection = context.actorSelection(s"/user/$actorName/elevator_$elevatorId")

        val stateFuture = elevatorActor ? RequestElevatorState(elevatorId)
        val elevatorState = Await.result(stateFuture, Duration.Inf).asInstanceOf[ElevatorState]

        val nextStop: Option[Floor] = elevatorControlSystem.findNextStop(stopsRequests.get(elevatorId).getOrElse(Queue[Floor]()), elevatorState.currentFloor, elevatorState.direction)
        if (nextStop.isDefined) {
          val nextStopFuture = elevatorActor ? MoveRequest(elevatorId, nextStop.get)
          val moveRequestSuccess = Await.result(nextStopFuture, Duration.Inf).asInstanceOf[MoveRequestSuccess]

          val makeMoveFuture = elevatorActor ? MakeMove(elevatorId, moveRequestSuccess.targetFloor)
          val makeMoveSuccess = Await.result(makeMoveFuture, Duration.Inf).asInstanceOf[MakeMoveSuccess]

          println(s"[BuildingCoordinator] Elevator ${makeMoveSuccess.elevatorId} arrived at floor [${makeMoveSuccess.floor}]")
          val stopsRequestsElevator = stopsRequests.get(elevatorId).getOrElse(Queue[Floor]())
          val newStopsRequestsElevator = stopsRequestsElevator.filterNot(_ == makeMoveSuccess.floor)
          val newStopsRequests = stopsRequests + (elevatorId -> newStopsRequestsElevator)

          val pickUpRequestsElevator = pickUpRequests.get(elevatorId).getOrElse(Queue[Floor]())
          val newPickUpRequestsElevator = {
            if (pickUpRequestsElevator.contains(makeMoveSuccess.floor)) {
              val dropOffFloor = BuildingUtil.generateRandomFloor(numberOfFloors, makeMoveSuccess.floor, direction)
              context.self ! DropOffRequest(makeMoveSuccess.elevatorId, dropOffFloor, direction)

              pickUpRequestsElevator.filterNot(_ == makeMoveSuccess.floor)
            } else {
              pickUpRequestsElevator
            }
          }
          val newPickUpRequests = pickUpRequests + (elevatorId -> newPickUpRequestsElevator)
          context.become(operational(currentElevatorId, newStopsRequests, newPickUpRequests))
        }
      }

    case msg@DropOffRequest(elevatorId, dropOffFloor, direction) =>
      println(s"[BuildingCoordinator] A passenger on [Elevator $elevatorId] requested $msg")
      val stopsRequestsElevator = stopsRequests.get(elevatorId).getOrElse(Queue[Floor]())
      val newStopsRequestsElevator = {
        if (!stopsRequestsElevator.contains(dropOffFloor)) stopsRequestsElevator.enqueue(dropOffFloor)
        else stopsRequestsElevator
      }
      val newStopsRequests = stopsRequests + (elevatorId -> newStopsRequestsElevator)
      context.become(operational(currentElevatorId, newStopsRequests, pickUpRequests))

      // passenger already in the elevator, just need to tell the elevator to move
      self ! MoveElevator(elevatorId, direction)

    case message => log.warning(s"[BuildingCoordinator] unknown message: $message")
  }

  /**
   * Create elevators [[org.github.felipegutierrez.elevatorsystem.actors.Elevator]] as child actors of the class
   * [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]].
   *
   * @param numberOfElevators
   * @return
   */
  def createElevators(numberOfElevators: Int): IndexedSeq[ActorRef] = {
    for (id <- 1 to numberOfElevators) yield {
      val elevator = context.actorOf(Elevator.props(id, s"elevator_$id"), s"elevator_$id")
      context.watch(elevator)
    }
  }
}
