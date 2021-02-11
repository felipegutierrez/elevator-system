package org.github.felipegutierrez.elevatorsystem.actors.protocol

/**
 * Messages that the Elevator actor can process.
 */
object ElevatorProtocol {
  case class MoveRequest(elevatorId: Int, floor: Int)
  case class MakeMove(elevatorId: Int, floor: Int)
  case class RequestElevatorState(elevatorId: Int)
}
