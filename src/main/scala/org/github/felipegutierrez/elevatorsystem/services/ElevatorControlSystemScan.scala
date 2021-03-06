package org.github.felipegutierrez.elevatorsystem.services

import org.github.felipegutierrez.elevatorsystem.actors.exceptions.ElevatorControlSystemException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.Direction
import org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorProtocol.Floor

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

  override def findNextStop(stopsRequested: Queue[Floor], currentFloor: Floor, direction: Direction): Option[Floor] = {
    if (stopsRequested.isEmpty) None
    else {
      var left = ListBuffer[Floor]()
      var right = ListBuffer[Floor]()

      stopsRequested.foreach { stop: Floor =>
        stop match {
          case stop if (stop < 0 || stop > numberOfFloors) => throw new ElevatorControlSystemException(s"it is not possible to stop at floor $stop because this building has only $numberOfFloors floors.")
          case stop if (stop == 0 || stop < currentFloor) => left += stop
          case stop if (stop == numberOfFloors || stop > currentFloor) => right += stop
        }
      }
      left = left.sorted
      right = right.sorted

      if (direction == Direction(-1)) {
        if (left.isEmpty) None
        else Option[Floor](left.last)
      }
      else {
        if (right.isEmpty) None
        else Option[Floor](right.head)
      }
    }
  }
}
