package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging, Props, Stash}
import akka.util.Timeout
import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol
import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.Direction
import org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorProtocol._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object Elevator {
  def props(id: ElevatorId, actorName: String) = Props(new Elevator(id, actorName))
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
case class Elevator(id: ElevatorId, actorName: String) extends Actor with ActorLogging with Stash {
  implicit val executionContext: ExecutionContext = context.dispatcher
  implicit val timeout = Timeout(3 seconds)


  override def receive(): Receive = stopped(0: Floor, 0: Floor, Direction(0))

  /**
   * Handler to receive messages when the Elevator is stopped
   *
   * @return
   */
  def stopped(currentFloor: Floor, targetFloor: Floor, direction: Direction): Receive = {
    case request@MoveRequest(elevatorId, newTargetFloor) =>
      println(s"[Elevator $id] $request received to floor $newTargetFloor")

      val newDirection: Direction = currentFloor match {
        case currentFloor if (currentFloor < newTargetFloor) => Direction(+1)
        case currentFloor if (currentFloor > newTargetFloor) => Direction(-1)
        case _ => Direction(0)
      }

      sender() ! BuildingCoordinatorProtocol.MoveRequestSuccess(elevatorId, newTargetFloor)

      unstashAll() // unstash the messages and change the handler
      context.become(moving(currentFloor, newTargetFloor, newDirection))
    case msg@RequestElevatorState(elevatorId) =>
      println(s"[Elevator $id] RequestElevatorState received")
      sender() ! BuildingCoordinatorProtocol.ElevatorState(elevatorId, currentFloor, targetFloor, direction)
    case msg@MakeMove(_, _) =>
      println(s"[Elevator $id] $msg stashed because the elevator is stopped!")
      stash()
    case message => log.warning(s"[Elevator $id] unknown message: $message")
  }

  /**
   * Handler to receive messages when the Elevator is moving
   *
   * @return
   */
  def moving(currentFloor: Floor, targetFloor: Floor, direction: Direction): Receive = {
    case msg@MakeMove(elevatorId, newTargetFloor) =>
      // println(s"[Elevator $actorId] $msg received")
      val milliseconds = currentFloor match {
        case currentFloor if (currentFloor < newTargetFloor) => (newTargetFloor - currentFloor) * 10
        case currentFloor if (currentFloor > newTargetFloor) => (currentFloor - newTargetFloor) * 10
        case _ => 0
      }

      // we already change the handler so every message receives after this line will be stashed because the Elevator is moving
      println(s"[Elevator $id] moving from floor $currentFloor to floor $newTargetFloor in ${milliseconds} milliseconds ...\n")
      Thread.sleep(milliseconds)

      sender() ! BuildingCoordinatorProtocol.MakeMoveSuccess(elevatorId, newTargetFloor, direction)

      unstashAll() // unstash the messages and change the handler
      context.become(stopped(newTargetFloor, newTargetFloor, Direction(0)))
    case msg@(MoveRequest(_, _) | RequestElevatorState(_)) =>
      println(s"[Elevator $id] $msg stashed because the elevator is moving!")
      stash()
    case message => log.warning(s"[Elevator $id] unknown message: $message")
  }
}
