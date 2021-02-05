package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging}
import org.github.felipegutierrez.elevatorsystem.actors.messages.Messages.{RequestPickUp, ResponsePickUp}

/**
 * This is the elevator panel of the building where someone can send pick_up messages to the Elevator
 */
class ElevatorPanel extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg@RequestPickUp(buildingActorRef, pickUpFloor, direction) =>
      log.info(s"received pick_up message: ${msg.toString}")
      self ! ResponsePickUp(1)
    case ResponsePickUp(elevatorId) =>
      log.info(s"receives response pick_up: ${elevatorId.toString}")
    case msg => log.warning(s"received unknown message $msg")
  }
}
