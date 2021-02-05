package org.github.felipegutierrez.elevatorsystem.actors.messages

import akka.actor.ActorRef

object Messages {

  trait BuildingMsg

  case class RequestPickUp(buildingActor: ActorRef, pickUpFloor: Int, direction: Int) extends BuildingMsg {
    override def toString: String = s"buildingActor: $buildingActor pickUpFloor: $pickUpFloor direction: $direction"
  }

  case class ResponsePickUp(elevatorId: Int) extends BuildingMsg {

  }

}
