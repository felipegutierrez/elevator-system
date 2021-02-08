package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging, Stash}
import akka.pattern.ask
import akka.util.Timeout
import org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol._

import scala.concurrent.duration._

/**
 * The [[org.github.felipegutierrez.elevatorsystem.actors.Elevator]] actor that is created by the
 * [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]]. It uses to handlers
 * to handle messages that define when the elevator is stopper or it is moving. When the elevator
 * is stooped it can receive [[org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol.MoveRequest]]
 * messages and change its handles to moving. When the elevator is moving it cannot respond to
 * [[org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol.MoveRequest]] messages,
 * although it stashes the received messages to be executed in the future.
 * The akka.actor.Stash is used to achieve this feature.
 *
 */
class Elevator extends Actor with ActorLogging with Stash {
  var currentFloor = 0
  var targetFloor = 0
  var direction = 0

  implicit val timeout = Timeout(3 seconds)

  override def receive(): Receive = stopped()

  def stopped(): Receive = {
    case request@MoveRequest(elevatorId, floor) =>
      println(s"[Elevator] MoveRequest received to floor $floor")
      unstashAll()
      context.become(moving())

      targetFloor = floor
      if (currentFloor < targetFloor) direction = +1
      else if (currentFloor > targetFloor) direction = -1
      else direction = 0

      // we already change the handler so every message receives after this line will be stashed because the Elevator is moving
      val milliseconds = (if (currentFloor < targetFloor) targetFloor - currentFloor else currentFloor - targetFloor) * 100
      println(s"[Elevator] I am moving from floor $currentFloor to floor $targetFloor in $milliseconds milliseconds ...\n")
      Thread.sleep(milliseconds)
      self ! MoveDone(elevatorId, targetFloor)
    case RequestElevatorState(elevatorId) =>
      println(s"[Elevator] RequestElevatorState received")
      sender() ! ElevatorState(elevatorId, currentFloor, targetFloor, direction)
    case message =>
      println(s"[Elevator] cannot process $message because it is stopped!")
      stash()
  }

  def moving(): Receive = {
    case msg@MoveDone(elevatorId, target) =>
      println(s"[Elevator] $msg")
      currentFloor = target
      val oldDirection = direction
      direction = 0
      // changing the handler to stop before to send the move success to the parent
      context.parent ? MoveRequestSuccess(elevatorId, target, oldDirection)
      unstashAll()
      context.become(stopped())
    case message =>
      println(s"[Elevator] cannot process $message because it is moving!")
      stash()
  }
}
