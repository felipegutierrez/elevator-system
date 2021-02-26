package org.github.felipegutierrez.elevatorsystem.services

import org.github.felipegutierrez.elevatorsystem.actors.exceptions.ElevatorControlSystemException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.Direction
import org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorProtocol.Floor

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

  override def findNextStop(stopsRequested: Queue[Floor], currentFloor: Floor = 0, direction: Direction = Direction(1)): Option[Floor] = {
    // println(s"[ElevatorControlSystemFCFS] next stop from list: ${stopsRequested.map(x => s"$x , ").mkString}")
    val nextStop: Option[Floor] = if (stopsRequested.isEmpty) None else Option[Floor](stopsRequested.head)
    if (nextStop.getOrElse(0) > numberOfFloors) throw ElevatorControlSystemException(s"it is not possible to stop at floor $nextStop because this building has only $numberOfFloors floors.")
    nextStop
  }
}
