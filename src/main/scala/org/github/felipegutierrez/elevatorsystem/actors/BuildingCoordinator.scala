package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.github.felipegutierrez.elevatorsystem.actors.exceptions.BuildingCoordinatorException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.{BuildingCoordinatorProtocol, ElevatorPanelProtocol, ElevatorProtocol}
import org.github.felipegutierrez.elevatorsystem.actors.util.BuildingUtil
import org.github.felipegutierrez.elevatorsystem.services._

import scala.collection.immutable.Queue
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

object BuildingCoordinator {
  def props(actorName: String = "buildingCoordinatorActor",
            numberOfFloors: Int = 10,
            numberOfElevators: Int = 1,
            elevatorControlSystemType: ElevatorControlSystem.ElevatorControlSystemType = ElevatorControlSystem.FCFSControlSystem) = {

    if (numberOfElevators < 0 || numberOfElevators > 16) throw new BuildingCoordinatorException("Number of elevators must be between 1 and 16")
    if (numberOfFloors < 2) throw new BuildingCoordinatorException("This is not a building. It is a house")

    val elevatorControlSystem: ElevatorControlSystem = {
      if (elevatorControlSystemType == ElevatorControlSystem.FCFSControlSystem)
        new ElevatorControlSystemFCFS(numberOfFloors, numberOfElevators)
      else if (elevatorControlSystemType == ElevatorControlSystem.ScanControlSystem)
        new ElevatorControlSystemScan(numberOfFloors, numberOfElevators)
      else
        throw new RuntimeException("Elevator system type unimplemented")
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

  // implicit val executionContext: ExecutionContext = context.system.dispatchers.lookup("building-coordinator-dispatcher")

  implicit val timeout = Timeout(10 seconds)

  val elevators = createElevators(numberOfElevators)

  override def receive: Receive = operational(Map[Int, Queue[Int]](), Map[Int, Queue[Int]]())

  def operational(stopsRequests: Map[Int, Queue[Int]], pickUpRequests: Map[Int, Queue[Int]]): Receive = {

    case request@BuildingCoordinatorProtocol.PickUpRequest(pickUpFloor, direction) =>
      println(s"[BuildingCoordinator] received a $request from floor[$pickUpFloor] to go [$direction] and will find an elevator to send.")

      if (pickUpFloor > numberOfFloors || pickUpFloor < 0) throw new BuildingCoordinatorException(s"I cannot pick up you because the floor $pickUpFloor does not exist in this building")
      if (direction != +1 && direction != -1) throw new BuildingCoordinatorException("the directions that this elevators supports are only: up [+1] and down [-1]")
      if (pickUpFloor == 0 && direction == -1) throw new BuildingCoordinatorException("you cannot go down because you are on the ground floor.")
      if (pickUpFloor == numberOfFloors && direction == +1) throw new BuildingCoordinatorException("you cannot go up because you are on the last floor.")

      val elevatorId = elevatorControlSystem.nextElevatorUsingRoundRobin()

      // change the Elevators state and calling context.become
      val stopsRequestsElevator = stopsRequests.get(elevatorId).getOrElse(Queue[Int]())
      val newStopsRequestsElevator = {
        if (!stopsRequestsElevator.contains(pickUpFloor)) stopsRequestsElevator.enqueue(pickUpFloor)
        else stopsRequestsElevator
      }
      val newStopsRequests = stopsRequests + (elevatorId -> newStopsRequestsElevator)

      val pickUpRequestsElevator = pickUpRequests.get(elevatorId).getOrElse(Queue[Int]())
      val newPickUpRequestsElevator = {
        if (!pickUpRequestsElevator.contains(pickUpFloor)) pickUpRequestsElevator.enqueue(pickUpFloor)
        else pickUpRequestsElevator
      }
      val newPickUpRequests = pickUpRequests + (elevatorId -> newPickUpRequestsElevator)

      context.become(operational(newStopsRequests, newPickUpRequests))

      sender() ! ElevatorPanelProtocol.PickUpRequestSuccess()

      self ! BuildingCoordinatorProtocol.MoveElevator(elevatorId, direction)

    case msg@BuildingCoordinatorProtocol.MoveElevator(elevatorId, direction) =>
      println(s"[BuildingCoordinator] received $msg")
      // it is possible to have duplicate requests to the same floor and we don't have to call the elevator more than once
      if (stopsRequests.get(elevatorId).nonEmpty) {
        val elevatorActor: ActorSelection = context.actorSelection(s"/user/$actorName/elevator_$elevatorId")

        val stateFuture = elevatorActor ? ElevatorProtocol.RequestElevatorState(elevatorId)
        val elevatorState = Await.result(stateFuture, Duration.Inf).asInstanceOf[BuildingCoordinatorProtocol.ElevatorState]

        val nextStop = elevatorControlSystem.findNextStop(stopsRequests.get(elevatorId).getOrElse(Queue[Int]()), elevatorState.currentFloor, elevatorState.direction)
        if (nextStop != -1) { // check if the queue is empty
          val nextStopFuture = elevatorActor ? ElevatorProtocol.MoveRequest(elevatorId, nextStop)
          val moveRequestSuccess = Await.result(nextStopFuture, Duration.Inf).asInstanceOf[BuildingCoordinatorProtocol.MoveRequestSuccess]

          val makeMoveFuture = elevatorActor ? ElevatorProtocol.MakeMove(elevatorId, moveRequestSuccess.targetFloor)
          val makeMoveSuccess = Await.result(makeMoveFuture, Duration.Inf).asInstanceOf[BuildingCoordinatorProtocol.MakeMoveSuccess]

          println(s"[BuildingCoordinator] Elevator ${makeMoveSuccess.elevatorId} arrived at floor [${makeMoveSuccess.floor}]")
          val stopsRequestsElevator = stopsRequests.get(elevatorId).getOrElse(Queue[Int]())
          val newStopsRequestsElevator = stopsRequestsElevator.filterNot(_ == makeMoveSuccess.floor)
          val newStopsRequests = stopsRequests + (elevatorId -> newStopsRequestsElevator)

          val pickUpRequestsElevator = pickUpRequests.get(elevatorId).getOrElse(Queue[Int]())
          val newPickUpRequestsElevator = {
            if (pickUpRequestsElevator.contains(makeMoveSuccess.floor)) {
              val dropOffFloor = BuildingUtil.generateRandomFloor(numberOfFloors, makeMoveSuccess.floor, direction)
              context.self ! BuildingCoordinatorProtocol.DropOffRequest(makeMoveSuccess.elevatorId, dropOffFloor, direction)

              pickUpRequestsElevator.filterNot(_ == makeMoveSuccess.floor)
            } else {
              pickUpRequestsElevator
            }
          }
          val newPickUpRequests = pickUpRequests + (elevatorId -> newPickUpRequestsElevator)
          context.become(operational(newStopsRequests, newPickUpRequests))
        }
      }

    case msg@BuildingCoordinatorProtocol.DropOffRequest(elevatorId, dropOffFloor, direction) =>
      println(s"[BuildingCoordinator] A passenger on [Elevator $elevatorId] requested $msg")
      val stopsRequestsElevator = stopsRequests.get(elevatorId).getOrElse(Queue[Int]())
      val newStopsRequestsElevator = {
        if (!stopsRequestsElevator.contains(dropOffFloor)) stopsRequestsElevator.enqueue(dropOffFloor)
        else stopsRequestsElevator
      }
      val newStopsRequests = stopsRequests + (elevatorId -> newStopsRequestsElevator)
      context.become(operational(newStopsRequests, pickUpRequests))

      // passenger already in the elevator, just need to tell the elevator to move
      self ! BuildingCoordinatorProtocol.MoveElevator(elevatorId, direction)

    case message => println(s"[BuildingCoordinator] unknown message: $message")
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
