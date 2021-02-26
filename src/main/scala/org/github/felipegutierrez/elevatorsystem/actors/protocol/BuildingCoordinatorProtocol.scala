package org.github.felipegutierrez.elevatorsystem.actors.protocol

import org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorProtocol.{ElevatorId, Floor}

/**
 * Messages that the BuildingCoordinator actor can process.
 */
object BuildingCoordinatorProtocol {

  case class Direction(direction: Int)
  object Direction {
    def apply(direction: Int): Direction = {
      direction match {
        case i if (i >= -1 && i <= +1) => new Direction(i)
      }
    }
  }
  case class PickUpRequest(pickUpFloor: Floor, direction: Direction)
  case class MoveElevator(elevatorId: ElevatorId, direction: Direction)
  case class MoveRequestSuccess(elevatorId: ElevatorId, targetFloor: Floor)
  case class MakeMoveSuccess(elevatorId: ElevatorId, floor: Floor, direction: Direction)
  case class ElevatorState(elevatorId: ElevatorId, currentFloor: Floor, targetFloor: Floor, direction: Direction)
  case class DropOffRequest(elevatorId: ElevatorId, dropOffFloor: Floor, direction: Direction)
}
