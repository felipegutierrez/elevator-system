package org.github.felipegutierrez.elevatorsystem.services

import org.github.felipegutierrez.elevatorsystem.actors.exceptions.ElevatorControlSystemException

import scala.collection.immutable.Queue
import scala.collection.mutable.ListBuffer

/**
 * * This is an elevator control system that uses the
 * [[https://en.wikipedia.org/wiki/Elevator_algorithm SCAN algorithm]] algorithm to find the
 * next stops on a list of requested stops.
 *
 * @param numberOfFloors
 * @param numberOfElevators
 */
class ElevatorControlSystemScan(numberOfFloors: Int, numberOfElevators: Int)
  extends ElevatorControlSystem(numberOfFloors, numberOfElevators) {

  override def findNextStop(stopsRequested: Queue[Int], currentFloor: Int, direction: Int): Int = {
    if (stopsRequested.isEmpty) -1
    else {
      var left = ListBuffer[Int]()
      var right = ListBuffer[Int]()

      stopsRequested.foreach { stop =>
        if (stop > numberOfFloors) throw ElevatorControlSystemException(s"it is not possible to stop at floor $stop because this building has only $numberOfFloors floors.")
        val res = (if (stop < currentFloor) "left" else if (stop > currentFloor) "right" else "wrong")

        res match {
          case "left" => left += stop
          case "right" => right += stop
          case "wrong" => throw ElevatorControlSystemException("Error on the Elevator control system SCAN algorithm.")
        }
      }
      left = left.sorted
      right = right.sorted

      if (direction == -1) {
        if (left.isEmpty) -1
        else left.last
      }
      else {
        if (right.isEmpty) -1
        else right.head
      }
    }
  }
}
