package org.github.felipegutierrez.elevatorsystem.services

import scala.collection.immutable.Queue

class ElevatorControlSystemFCFS(numberOfFloors: Int, numberOfElevators: Int)
  extends ElevatorControlSystem(numberOfFloors, numberOfElevators) {

  override def findNextStop(stopsRequested: Queue[Int], currentFloor: Int = 0, direction: Int = 1): Int = {
    // println(s"[ElevatorControlSystemFCFS] next stop from list: ${stopsRequested.map(x => s"$x , ").mkString}")
    if (stopsRequested.isEmpty) -1
    else stopsRequested.head
  }
}
