package org.github.felipegutierrez.elevatorsystem.actors.protocol

import akka.actor.ActorRef

object Protocol {

  trait ElevatorPanelProtocol
  case class PickUp(pickUpFloor: Int, direction: Int, buildingActor: ActorRef) extends ElevatorPanelProtocol
  case class PickUpRequestSuccess() extends ElevatorPanelProtocol
  case class PickUpRequestFailure() extends ElevatorPanelProtocol

  trait BuildingProtocol
  case class Initialize() extends BuildingProtocol
  case class PickUpRequest(pickUpFloor: Int, direction: Int) extends BuildingProtocol
  case class MoveElevator(elevatorId: Int) extends BuildingProtocol
  case class MoveElevatorSuccess(elevatorId: Int, floor: Int) extends BuildingProtocol
  case class MoveElevatorFailure(reason: Throwable) extends BuildingProtocol
  case class MoveRequestSuccess(elevatorId: Int, floor: Int, direction: Int) extends BuildingProtocol
  case class ElevatorState(elevatorId: Int, currentFloor: Int, targetFloor: Int, direction: Int) extends BuildingProtocol
  case class DropOffRequest(elevatorId: Int, dropOffFloor: Int) extends BuildingProtocol

  trait ElevatorProtocol
  case class MoveRequest(elevatorId: Int, floor: Int) extends ElevatorProtocol
  case class RequestElevatorState(elevatorId: Int) extends ElevatorProtocol
  case class MoveDone(elevatorId: Int, targetFloor: Int) extends ElevatorProtocol
  case class ElevatorInfo(msg: String) extends ElevatorProtocol

}
