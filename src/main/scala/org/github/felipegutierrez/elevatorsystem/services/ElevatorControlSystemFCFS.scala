package org.github.felipegutierrez.elevatorsystem.services

import scala.collection.immutable.Queue

/**
 * This is an elevator control system that uses the
 * [[https://en.wikipedia.org/wiki/FIFO_(computing_and_electronics) FCFS algorithm]] algorithm to find the
 * next stops on a list of requested stops.
 *
 * @param numberOfFloors
 * @param numberOfElevators
 */
class ElevatorControlSystemFCFS(numberOfFloors: Int, numberOfElevators: Int)
  extends ElevatorControlSystem(numberOfFloors, numberOfElevators) {

  override def findNextStop(stopsRequested: Queue[Int], currentFloor: Int = 0, direction: Int = 1): Int = {
    // println(s"[ElevatorControlSystemFCFS] next stop from list: ${stopsRequested.map(x => s"$x , ").mkString}")
    if (stopsRequested.isEmpty) -1
    else stopsRequested.head
  }
}
