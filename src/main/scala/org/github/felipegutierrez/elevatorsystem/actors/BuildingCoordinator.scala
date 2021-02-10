package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.github.felipegutierrez.elevatorsystem.actors.exceptions.BuildingCoordinatorException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.{BuildingCoordinatorProtocol, ElevatorPanelProtocol, ElevatorProtocol}
import org.github.felipegutierrez.elevatorsystem.services.{ElevatorControlSystem, ElevatorControlSystemFCFS}

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.Random

object BuildingCoordinator {
  def props(actorName: String = "buildingCoordinatorActor",
            numberOfFloors: Int = 10,
            numberOfElevators: Int = 1,
            elevatorControlSystem: ElevatorControlSystem = new ElevatorControlSystemFCFS(1)) = {
    if (numberOfElevators < 0 || numberOfElevators > 16) throw new BuildingCoordinatorException("Number of elevators must be between 1 and 16")
    if (numberOfFloors < 2) throw new BuildingCoordinatorException("This is not a building. It is a house")
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

  import context.dispatcher

  implicit val timeout = Timeout(10 seconds)

  val elevators = createElevators(numberOfElevators)

  var stopsRequests = mutable.Map[Int, Set[Int]]()
  var pickUpRequests = mutable.Map[Int, Set[Int]]()

  override def receive: Receive = {

    case request@BuildingCoordinatorProtocol.PickUpRequest(pickUpFloor, direction) =>
      println(s"[BuildingCoordinator] received a $request from floor[$pickUpFloor] to go [$direction] and will find an elevator to send.")

      if (pickUpFloor > numberOfFloors || pickUpFloor < 0) throw new BuildingCoordinatorException(s"I cannot pick up you because the floor $pickUpFloor does not exist in this building")
      if (direction != +1 && direction != -1) throw new BuildingCoordinatorException("the directions that this elevators supports are only: up [+1] and down [-1]")
      if (pickUpFloor == 1 && direction == -1) throw new BuildingCoordinatorException("you cannot go down because you are on the first floor.")
      if (pickUpFloor == numberOfFloors && direction == +1) throw new BuildingCoordinatorException("you cannot go up because you are on the last floor.")

      val elevatorId = elevatorControlSystem.nextElevatorUsingRoundRobin()

      addStopRequest(elevatorId, pickUpFloor)
      addPickUpRequest(elevatorId, pickUpFloor)

      self ! BuildingCoordinatorProtocol.MoveElevator(elevatorId)
      sender() ! ElevatorPanelProtocol.PickUpRequestSuccess()

    case msg@BuildingCoordinatorProtocol.MoveElevator(elevatorId) =>
      println(s"[BuildingCoordinator] received $msg")
      val elevatorActor: ActorSelection = context.actorSelection(s"/user/$actorName/elevator_$elevatorId")
      val stateFuture = elevatorActor ? ElevatorProtocol.RequestElevatorState(elevatorId)
      stateFuture
        .mapTo[BuildingCoordinatorProtocol.ElevatorState]
        .map { state: BuildingCoordinatorProtocol.ElevatorState =>
          val nextStop = elevatorControlSystem.findNextStop(stopsRequests.get(elevatorId).get)
          val requestFuture = elevatorActor ? ElevatorProtocol.MoveRequest(elevatorId, nextStop)
          requestFuture
            .mapTo[BuildingCoordinatorProtocol.MoveRequestSuccess]
            .map { moveRequestSuccess =>
              val makeMoveFuture = elevatorActor ? ElevatorProtocol.MakeMove(elevatorId, nextStop)
              makeMoveFuture
                .mapTo[BuildingCoordinatorProtocol.MakeMoveSuccess]
                .map { makeMoveSuccess: BuildingCoordinatorProtocol.MakeMoveSuccess =>
                  println(s"[BuildingCoordinator] Elevator ${makeMoveSuccess.elevatorId} arrived at floor [${makeMoveSuccess.floor}]")
                  removeStopRequest(makeMoveSuccess.elevatorId, makeMoveSuccess.floor)
                  // If the floor was a PickUpRequest we should ask the following message DropOffRequest
                  if (existPickUpRequest(makeMoveSuccess.elevatorId, makeMoveSuccess.floor)) {
                    removePickUpRequest(makeMoveSuccess.elevatorId, makeMoveSuccess.floor)
                    val dropOffFloor = generateRandomFloor(makeMoveSuccess.floor, makeMoveSuccess.direction)
                    val dropOffMsg = BuildingCoordinatorProtocol.DropOffRequest(makeMoveSuccess.elevatorId, dropOffFloor)
                    // println(s"[BuildingCoordinator] A passenger on [Elevator ${makeMoveSuccess.elevatorId}] request a $dropOffMsg")
                    self ! dropOffMsg
                  }
                }
            }
        }

    case msg@BuildingCoordinatorProtocol.DropOffRequest(elevatorId, dropOffFloor) =>
      println(s"[BuildingCoordinator] A passenger on [Elevator $elevatorId] requested $msg")
      addStopRequest(elevatorId, dropOffFloor)
      // passenger already in the elevator, just need to tell the elevator to move
      self ! BuildingCoordinatorProtocol.MoveElevator(elevatorId)

    case message => println(s"[BuildingCoordinator] unknown message: $message")
  }

  def addStopRequest(elevatorId: Int, stop: Int) = {
    var value: Set[Int] = stopsRequests.get(elevatorId).getOrElse(Set[Int]())
    value += stop
    stopsRequests.update(elevatorId, value)
  }

  def removeStopRequest(elevatorId: Int, stop: Int) = {
    var value: Set[Int] = stopsRequests.get(elevatorId).getOrElse(Set[Int]())
    value -= stop
    stopsRequests.update(elevatorId, value)
  }

  def addPickUpRequest(elevatorId: Int, stop: Int) = {
    var value: Set[Int] = pickUpRequests.get(elevatorId).getOrElse(Set[Int]())
    value += stop
    pickUpRequests.update(elevatorId, value)
  }

  def removePickUpRequest(elevatorId: Int, stop: Int) = {
    var value: Set[Int] = pickUpRequests.get(elevatorId).getOrElse(Set[Int]())
    value -= stop
    pickUpRequests.update(elevatorId, value)
  }

  def existPickUpRequest(elevatorId: Int, stop: Int): Boolean = {
    pickUpRequests.getOrElse(elevatorId, Set[Int]()).contains(stop)
  }

  /**
   * Generate a random floor to request the message
   * [[org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.DropOffRequest]].
   *
   * @param floor
   * @param direction
   * @return
   */
  def generateRandomFloor(floor: Int, direction: Int): Int = {
    if (direction > 0) {
      floor + Random.nextInt((numberOfFloors - floor) + 1)
    } else {
      if (floor == 1) 0
      else Random.nextInt(floor - 1)
    }
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
