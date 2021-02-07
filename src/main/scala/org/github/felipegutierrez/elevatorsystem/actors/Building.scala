package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging, ActorSelection, Props}
import akka.pattern.pipe
import akka.util.Timeout
import org.github.felipegutierrez.elevatorsystem.actors.exceptions.BuildingException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol._
import org.github.felipegutierrez.elevatorsystem.services.{ElevatorControlSystem, ElevatorControlSystemFCFS}

import scala.concurrent.duration._

object Building {
  def props(numberOfFloors: Int = 10,
            numberOfElevators: Int = 1,
            elevatorControlSystem: ElevatorControlSystem = new ElevatorControlSystemFCFS()) = {
    if (numberOfElevators < 0 || numberOfElevators > 16) throw BuildingException("Number of elevators must be between 1 and 16")
    if (numberOfFloors < 2) throw BuildingException("This is not a building. It is a house")
    Props(new Building(numberOfFloors, numberOfElevators, elevatorControlSystem))
  }
}

case class Building(numberOfFloors: Int = 10,
                    numberOfElevators: Int = 1,
                    elevatorControlSystem: ElevatorControlSystem = new ElevatorControlSystemFCFS())
  extends Actor with ActorLogging {

  import context.dispatcher

  implicit val timeout = Timeout(3 seconds)

  val elevators = for (id <- 1 to numberOfElevators) yield context.actorOf(Props[Elevator], s"elevator_$id")

  override def receive: Receive = {
    case request@PickUpRequest(pickUpFloor, direction) =>
      println(s"[Building] building received a pick_up_request from floor[$pickUpFloor] to go [$direction] and will find an elevator to send.")
      println(s"[Building] add the pickup flor on the request pickup list.")
      println(s"[Building] use the control system to find an elevator")
      elevatorControlSystem.findElevator(pickUpFloor, direction).map(id => MoveElevator(id)).pipeTo(self)
      sender() ! PickUpRequestSuccess()
    case MoveElevator(elevatorId) =>
      println(s"[Building] received MoveElevator($elevatorId) I will move it")
      val elevatorActor: ActorSelection = context.actorSelection(s"/user/buildingActor/elevator_$elevatorId")
      elevatorActor ! MoveRequest()
    // TODO: where do I remove the elevator ID from the list of requested moves?
    case MoveElevatorFailure(exception) =>
      println(s"[Building] I could not move the elevator due to: $exception")
    case MoveRequestSuccess() =>
      println(s"[Building] MoveRequestSuccess received")
  }
}
