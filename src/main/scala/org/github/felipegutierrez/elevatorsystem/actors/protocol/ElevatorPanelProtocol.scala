package org.github.felipegutierrez.elevatorsystem.actors.protocol

import akka.actor.ActorRef

/**
 * Messages that the Elevator Panel actor can process.
 */
object ElevatorPanelProtocol {
  case class PickUp(pickUpFloor: Int, direction: Int, buildingActor: ActorRef)
  object PickUpRequestSuccess
  object PickUpRequestFailure
}
