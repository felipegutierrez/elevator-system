package org.github.felipegutierrez.elevatorsystem.services

import scala.collection.immutable.Queue
import scala.collection.mutable.ListBuffer

class ElevatorControlSystemScan(numberOfFloors: Int, numberOfElevators: Int)
  extends ElevatorControlSystem(numberOfFloors, numberOfElevators) {

  override def findNextStop(stopsRequested: Queue[Int], currentFloor: Int, direction: Int): Int = {
    if (stopsRequested.isEmpty) -1
    else {
      var left = ListBuffer[Int]()
      var right = ListBuffer[Int]()

      stopsRequested.foreach { stop =>
        if (stop < currentFloor) left += stop
        else if (stop > currentFloor) right += stop
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
