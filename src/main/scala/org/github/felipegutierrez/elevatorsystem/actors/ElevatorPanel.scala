package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging}
import org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol._

class ElevatorPanel extends Actor with ActorLogging {

  override def receive: Receive = {
    case PickUp(buildingActor) =>
      println(s"[ElevatorPanel] panel received a pick_up and will send the pick_up to the building")
      buildingActor ! PickUpRequest(self)
    case PickUpSuccess() =>
      println(s"[ElevatorPanel] pick_up successful")
  }
}
