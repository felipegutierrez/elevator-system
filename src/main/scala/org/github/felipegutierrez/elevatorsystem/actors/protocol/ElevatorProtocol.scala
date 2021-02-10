package org.github.felipegutierrez.elevatorsystem.actors.protocol

object ElevatorProtocol {
  case class MoveRequest(elevatorId: Int, floor: Int)
  case class MakeMove(elevatorId: Int, floor: Int)
  case class RequestElevatorState(elevatorId: Int)
}
