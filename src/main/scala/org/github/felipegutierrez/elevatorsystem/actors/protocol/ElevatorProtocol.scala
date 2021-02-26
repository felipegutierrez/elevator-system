package org.github.felipegutierrez.elevatorsystem.actors.protocol

/**
 * Messages that the Elevator actor can process.
 */
object ElevatorProtocol {
  type ElevatorId = Int
  type Floor = Int
  case class MoveRequest(elevatorId: ElevatorId, floor: Floor)
  case class MakeMove(elevatorId: ElevatorId, floor: Floor)
  case class RequestElevatorState(elevatorId: ElevatorId)
}
