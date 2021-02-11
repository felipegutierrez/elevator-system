package org.github.felipegutierrez.elevatorsystem.services

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class ElevatorControlSystemScan(numberOfFloors: Int, numberOfElevators: Int)
  extends ElevatorControlSystem(numberOfFloors, numberOfElevators) {

  override def findNextStop(stopsRequested: mutable.Queue[Int], currentFloor: Int, direction: Int): Int = {

    var left = ListBuffer[Int]()
    var right = ListBuffer[Int]()

    //    if (direction == -1) left += 0
    //    else if (direction == +1) right += (numberOfFloors - 1)

    stopsRequested.foreach { stop =>
      if (stop < currentFloor) left += stop
      else if (stop > currentFloor) right += stop
    }
    left = left.sorted
    right = right.sorted

    if (direction == -1) left.last
    else right.head
  }
}
