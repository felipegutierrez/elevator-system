package org.github.felipegutierrez.elevatorsystem.services

import scala.collection.mutable

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

abstract class ElevatorControlSystem(val numberOfFloors: Int, val numberOfElevators: Int) {

  var lastElevator = 0

  def nextElevatorUsingRoundRobin(): Int = {
    lastElevator += 1
    if (lastElevator > numberOfElevators) lastElevator = 1
    lastElevator
  }

  def findNextStop(stopsRequested: mutable.Queue[Int], currentFloor: Int, direction: Int): Int
}
