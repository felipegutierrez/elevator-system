package org.github.felipegutierrez.elevatorsystem.actors.protocol

import akka.actor.ActorRef
import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.Direction
import org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorProtocol.Floor

/**
 * Messages that the Elevator Panel actor can process.
 */
object ElevatorPanelProtocol {
  case class PickUp(pickUpFloor: Floor, direction: Direction, buildingActor: ActorRef)
  object PickUpRequestSuccess
  object PickUpRequestFailure
}
