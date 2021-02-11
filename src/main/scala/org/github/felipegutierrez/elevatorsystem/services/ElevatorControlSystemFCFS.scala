package org.github.felipegutierrez.elevatorsystem.services

import scala.collection.mutable

class ElevatorControlSystemFCFS(numberOfFloors: Int, numberOfElevators: Int)
  extends ElevatorControlSystem(numberOfFloors, numberOfElevators) {

  override def findNextStop(stopsRequested: mutable.Queue[Int], currentFloor: Int = 0, direction: Int = 1): Int = {
    // println(s"[ElevatorControlSystemFCFS] next stop from list: ${stopsRequested.map(x => s"$x , ").mkString}")
    stopsRequested.head
  }
}
