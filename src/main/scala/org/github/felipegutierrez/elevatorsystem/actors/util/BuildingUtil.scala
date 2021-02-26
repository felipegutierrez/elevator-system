package org.github.felipegutierrez.elevatorsystem.actors.util

import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.Direction
import org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorProtocol.Floor

import java.util.concurrent.ThreadLocalRandom

object BuildingUtil {

  /**
   * Generate a random floor to request the message
   * [[org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.DropOffRequest]]
   * based on the current floor and where the passenger wants to go (up = +1, or down = -1).
   * The random floor generated is limited by the number of floors that exist in the building.
   *
   * @param numberOfFloors the number of floors on the building
   * @param currentFloor   the current floor that the elevator is now
   * @param direction      the direction that the passenger send the pick up message
   * @return a random floor to create a drop off message
   */
  def generateRandomFloor(numberOfFloors: Int, currentFloor: Int, direction: Direction): Floor = {
    if (currentFloor >= numberOfFloors && direction.direction > 0) throw new RuntimeException("Wrong direction")
    if (currentFloor <= 0 && direction.direction < 0) throw new RuntimeException("Wrong direction")

    if (direction.direction > 0) { // direction is to go UP
      ThreadLocalRandom.current().nextInt(currentFloor + 1, numberOfFloors + 1)
    } else { // direction is to go DOWN
      if (currentFloor == 1) 0
      else ThreadLocalRandom.current().nextInt(0, currentFloor)
    }
  }
}
