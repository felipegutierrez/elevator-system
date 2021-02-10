package org.github.felipegutierrez.elevatorsystem.actors.protocol

object BuildingCoordinatorProtocol {

  case class PickUpRequest(pickUpFloor: Int, direction: Int)
  case class MoveElevator(elevatorId: Int)
  case class MoveRequestSuccess(elevatorId: Int, floor: Int)
  case class MakeMoveSuccess(elevatorId: Int, floor: Int, direction: Int)
  case class ElevatorState(elevatorId: Int, currentFloor: Int, targetFloor: Int, direction: Int)
  case class DropOffRequest(elevatorId: Int, dropOffFloor: Int)
}
