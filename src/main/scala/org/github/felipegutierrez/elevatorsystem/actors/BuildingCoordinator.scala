package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.github.felipegutierrez.elevatorsystem.actors.exceptions.BuildingException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol.{DropOffRequest, _}
import org.github.felipegutierrez.elevatorsystem.services.{ElevatorControlSystem, ElevatorControlSystemFCFS}

import scala.concurrent.duration._
import scala.util.Random

object BuildingCoordinator {
  def props(actorName: String = "buildingCoordinatorActor",
            numberOfFloors: Int = 10,
            numberOfElevators: Int = 1,
            elevatorControlSystem: ElevatorControlSystem = new ElevatorControlSystemFCFS()) = {
    if (numberOfElevators < 0 || numberOfElevators > 16) throw BuildingException("Number of elevators must be between 1 and 16")
    if (numberOfFloors < 2) throw BuildingException("This is not a building. It is a house")
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

  var stopsRequests = Set[Int]()
  var pickUpRequests = Set[Int]()

  override def receive: Receive = {

    case request@PickUpRequest(pickUpFloor, direction) =>
      println(s"[BuildingCoordinator] received a $request from floor[$pickUpFloor] to go [$direction] and will find an elevator to send.")
      // add the pickup flor on the request stop list.
      stopsRequests += pickUpFloor
      pickUpRequests += pickUpFloor
      val elevatorId = elevatorControlSystem.findElevator(pickUpFloor, direction)
      self ! MoveElevator(elevatorId)
      sender() ! PickUpRequestSuccess()

    case msg@MoveElevator(elevatorId) =>
      println(s"[BuildingCoordinator] received $msg")
      val elevatorActor: ActorSelection = context.actorSelection(s"/user/$actorName/elevator_$elevatorId")
      val stateFuture = elevatorActor ? RequestElevatorState(elevatorId)
      stateFuture
        .mapTo[ElevatorState]
        .map { state: ElevatorState =>
          val nextStop = elevatorControlSystem.findNextStop(stopsRequests)
          elevatorActor ! MoveRequest(elevatorId, nextStop)
        }

    case MoveRequestSuccess(elevatorId, floor, direction) =>
      println(s"[BuildingCoordinator] Elevator $elevatorId arrived at floor [$floor]")
      stopsRequests -= floor
      // If the floor was a PickUpRequest we should ask the following message DropOffRequest
      if (pickUpRequests.contains(floor)) {
        pickUpRequests -= floor
        val dropOffFloor = generateRandomFloor(floor, direction)
        val dropOffMsg = DropOffRequest(elevatorId, dropOffFloor)
        println(s"[BuildingCoordinator] A passenger request a $dropOffMsg")
        self ! dropOffMsg
      }

    case msg@DropOffRequest(elevatorId, dropOffFloor) =>
      println(s"[BuildingCoordinator] received DropOffRequest to floor [$dropOffFloor]")
      stopsRequests += dropOffFloor
      // passenger already in the elevator, just need to tell the elevator to move
      self ! MoveElevator(elevatorId)

    case MoveElevatorFailure(exception) =>
      println(s"[BuildingCoordinator] I could not move the elevator due to: $exception")
  }

  /**
   * Generate a random floor to request the message
   * [[org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol.DropOffRequest]].
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
