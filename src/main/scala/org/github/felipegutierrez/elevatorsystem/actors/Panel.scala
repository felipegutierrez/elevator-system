package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging}
import org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol._

/**
 * The [[org.github.felipegutierrez.elevatorsystem.actors.Panel]] actor is the entering point
 * to communicate with the [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]]
 * in order to call [[org.github.felipegutierrez.elevatorsystem.actors.Elevator]]s.
 * It is responsible to receive [[org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol.PickUp]]
 * from the [[org.github.felipegutierrez.elevatorsystem.Main]] application, send
 * [[org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol.PickUpRequest]] to the
 * [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]], and finally receive back a
 * [[org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol.PickUpRequestSuccess]] messages.
 */
class Panel extends Actor with ActorLogging {

  /**
   * The default handles of messages to interact with the
   * [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]]
   *
   * @return
   */
  override def receive: Receive = {
    case PickUp(pickUpFloor, direction, buildingActor) =>
      val msg = PickUpRequest(pickUpFloor, direction)
      println(s"[Panel] received a PickUp from floor [$pickUpFloor] to go [$direction], sending $msg to the building coordinator")
      buildingActor ! msg
    case PickUpRequestSuccess() =>
      println(s"[Panel] PickUpRequest was requested")
  }
}
