package org.github.felipegutierrez.elevatorsystem.actors.util

import scala.collection.immutable.Queue
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

  def run(args: Array[String]): Unit = {

    val stopsRequests = Map[Int, Queue[Int]]()
    val myQueue = Queue[Int]()
    val myQueue1 = myQueue.enqueue(1)
    val myQueue2 = myQueue1.enqueue(2)
    val myQueue3 = myQueue2.enqueue(3)
    val stopsRequests2 = stopsRequests + (1 -> myQueue3)
    val stopsRequests3 = stopsRequests2 + (2 -> myQueue3)
    stopsRequests3.foreach(x => println(s"$x "))

    stopsRequests3.get(3).getOrElse()
  }
}
