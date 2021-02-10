package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.util.Timeout
import org.github.felipegutierrez.elevatorsystem.actors.protocol.{BuildingCoordinatorProtocol, ElevatorProtocol}

import scala.concurrent.duration._

object Elevator {
  def props(actorId: Int, actorName: String) = Props(new Elevator(actorId, actorName))
}

/**
 * The [[org.github.felipegutierrez.elevatorsystem.actors.Elevator]] actor that is created by the
 * [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]]. It uses to handlers
 * to handle messages that define when the elevator is stopper or it is moving. When the elevator
 * is stooped it can receive [[org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorProtocol.MoveRequest]]
 * messages and change its handles to moving. When the elevator is moving it cannot respond to
 * [[org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorProtocol.MoveRequest]] messages,
 * although it stashes the received messages to be executed in the future.
 * The akka.actor.Stash is used to achieve this feature.
 *
 */
case class Elevator(actorId: Int, actorName: String) extends Actor with ActorLogging with Stash {
  var currentFloor = 0
  var targetFloor = 0
  var direction = 0
  implicit val timeout = Timeout(3 seconds)

  override def receive(): Receive = stopped()

  /**
   * Handler to receive messages when the Elevator is stopped
   *
   * @return
   */
  def stopped(): Receive = {
    case request@ElevatorProtocol.MoveRequest(elevatorId, floor) =>
      println(s"[Elevator $actorId] $request received to floor $floor")

      // unstash the messages and change the handler
      unstashAll()
      context.become(moving())

      // return the answer to the sender only after we changed the handler
      sender() ! BuildingCoordinatorProtocol.MoveRequestSuccess(elevatorId, floor)
    case msg@ElevatorProtocol.RequestElevatorState(elevatorId) =>
      println(s"[Elevator $actorId] RequestElevatorState received")
      sender() ! BuildingCoordinatorProtocol.ElevatorState(elevatorId, currentFloor, targetFloor, direction)
    case message =>
      println(s"[Elevator $actorId] msg $message stashed because the elevator is stopped!")
      stash()
  }

  /**
   * Handler to receive messages when the Elevator is moving
   *
   * @return
   */
  def moving(): Receive = {
    case msg@ElevatorProtocol.MakeMove(elevatorId, floor) =>
      println(s"[Elevator $actorId] $msg received")
      targetFloor = floor
      if (currentFloor < targetFloor) direction = +1
      else if (currentFloor > targetFloor) direction = -1
      else direction = 0

      // we already change the handler so every message receives after this line will be stashed because the Elevator is moving
      val milliseconds = (if (currentFloor < targetFloor) targetFloor - currentFloor else currentFloor - targetFloor) * 10
      println(s"[Elevator $actorId] moving from floor $currentFloor to floor $targetFloor in $milliseconds milliseconds ...\n")
      Thread.sleep(milliseconds)

      currentFloor = targetFloor
      val oldDirection = direction
      direction = 0

      sender() ! BuildingCoordinatorProtocol.MakeMoveSuccess(elevatorId, targetFloor, oldDirection)
      // unstash the messages and change the handler
      unstashAll()
      context.become(stopped())
    case message =>
      println(s"[Elevator $actorId] msg $message stashed because the elevator is moving!")
      stash()
  }
}
