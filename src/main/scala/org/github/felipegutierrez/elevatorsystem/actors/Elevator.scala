package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.util.Timeout
import org.github.felipegutierrez.elevatorsystem.actors.protocol.{BuildingCoordinatorProtocol, ElevatorProtocol}

import scala.concurrent.ExecutionContext
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
  implicit val executionContext: ExecutionContext = context.dispatcher
  implicit val timeout = Timeout(3 seconds)

  override def receive(): Receive = stopped(0, 0, 0)

  /**
   * Handler to receive messages when the Elevator is stopped
   *
   * @return
   */
  def stopped(currentFloor: Int, targetFloor: Int, direction: Int): Receive = {
    case request@ElevatorProtocol.MoveRequest(elevatorId, newTargetFloor) =>
      println(s"[Elevator $actorId] $request received to floor $newTargetFloor")

      val newDirection = if (currentFloor < newTargetFloor) +1
      else if (currentFloor > newTargetFloor) -1
      else 0

      sender() ! BuildingCoordinatorProtocol.MoveRequestSuccess(elevatorId, newTargetFloor)

      unstashAll() // unstash the messages and change the handler
      context.become(moving(currentFloor, newTargetFloor, newDirection))
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
  def moving(currentFloor: Int, targetFloor: Int, direction: Int): Receive = {
    case msg@ElevatorProtocol.MakeMove(elevatorId, newTargetFloor) =>
      // println(s"[Elevator $actorId] $msg received")
      val milliseconds =
        if (currentFloor < newTargetFloor) (newTargetFloor - currentFloor) * 10
        else if (currentFloor > newTargetFloor) (currentFloor - newTargetFloor) * 10
        else 0

      // we already change the handler so every message receives after this line will be stashed because the Elevator is moving
      println(s"[Elevator $actorId] moving from floor $currentFloor to floor $newTargetFloor in ${milliseconds} milliseconds ...\n")
      Thread.sleep(milliseconds)

      sender() ! BuildingCoordinatorProtocol.MakeMoveSuccess(elevatorId, newTargetFloor, direction)

      unstashAll() // unstash the messages and change the handler
      context.become(stopped(newTargetFloor, newTargetFloor, 0))
    case message =>
      println(s"[Elevator $actorId] msg $message stashed because the elevator is moving!")
      stash()
  }
}
