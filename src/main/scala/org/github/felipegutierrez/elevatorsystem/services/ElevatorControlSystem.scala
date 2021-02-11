package org.github.felipegutierrez.elevatorsystem.services

import scala.collection.immutable.Queue

object ElevatorControlSystem {

  /**
   * Types to define the logic used in the Elevator control system
   */
  sealed trait ElevatorControlSystemType

  /**
   * This type defines the use of the FirstComeFirstServe logic
   */
  case object FCFSControlSystem extends ElevatorControlSystemType

  /**
   * This type defines the use of the SCAN logic
   */
  case object ScanControlSystem extends ElevatorControlSystemType

}

/**
 * The abstract class for the control system of Elevators. The BuildingCoordinator must use one
 * ElevatorControlSystem that implements the methods of this abstract class.
 *
 * @param numberOfFloors
 * @param numberOfElevators
 */
abstract class ElevatorControlSystem(val numberOfFloors: Int, val numberOfElevators: Int) {

  var lastElevator = 0

  def nextElevatorUsingRoundRobin(): Int = {
    lastElevator += 1
    if (lastElevator > numberOfElevators) lastElevator = 1
    lastElevator
  }

  def findNextStop(stopsRequested: Queue[Int], currentFloor: Int, direction: Int): Int
}
