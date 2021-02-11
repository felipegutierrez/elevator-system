package org.github.felipegutierrez.elevatorsystem.actors.util

import scala.util.Random

object BuildingUtil {

  /**
   * Generate a random floor to request the message
   * [[org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.DropOffRequest]].
   *
   * @param numberOfFloors
   * @param currentFloor
   * @param direction
   * @return
   */
  def generateRandomFloor(numberOfFloors: Int, currentFloor: Int, direction: Int): Int = {
    if (currentFloor >= numberOfFloors && direction > 0) throw new RuntimeException("Wrong direction")
    if (currentFloor <= 0 && direction < 0) throw new RuntimeException("Wrong direction")

    if (direction > 0) { // direction is to go UP
      currentFloor + Random.nextInt((numberOfFloors - currentFloor) + 1)
    } else { // direction is to go DOWN
      if (currentFloor == 1) 0
      else Random.nextInt(currentFloor - 1)
    }
  }
}
