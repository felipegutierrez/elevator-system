package org.github.felipegutierrez.elevatorsystem.actors.protocol

import akka.actor.ActorRef

object ElevatorPanelProtocol {
  case class PickUp(pickUpFloor: Int, direction: Int, buildingActor: ActorRef)
  case class PickUpRequestSuccess()
  case class PickUpRequestFailure()
}
