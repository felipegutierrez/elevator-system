package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging}
import org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol._

class Elevator extends Actor with ActorLogging {
  override def receive: Receive = {
    case request@MoveRequest() =>
      println(s"[Elevator ${self}] Ok sender [${sender()}], I will move")
      println(s"[Elevator ${self}] ")
      sender() ! MoveRequestSuccess()
  }
}
