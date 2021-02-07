package org.github.felipegutierrez.elevatorsystem.actors.protocol

import akka.actor.ActorRef

object Protocol {

  trait ElevatorPanelProtocol
  case class PickUp(buildingActor: ActorRef) extends ElevatorPanelProtocol
  case class PickUpSuccess() extends ElevatorPanelProtocol
  // case class PickUpResponse() extends ElevatorProtocol
  // case class PickUpResponseSuccess() extends ElevatorProtocol

  trait BuildingProtocol
  case class Initialize() extends BuildingProtocol
  case class PickUpRequest(elevatorPanelActor: ActorRef) extends BuildingProtocol
  case class MoveElevator(elevatorId: Int) extends BuildingProtocol
  case class MoveElevatorFailure(reason: Throwable) extends BuildingProtocol
  case class MoveRequestSuccess() extends BuildingProtocol

  trait ElevatorProtocol
  case class MoveRequest() extends ElevatorProtocol

}
