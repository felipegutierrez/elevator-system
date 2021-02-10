package org.github.felipegutierrez.elevatorsystem.services

abstract class ElevatorControlSystem(val numberOfFloors: Int, val numberOfElevators: Int) {

  var lastElevator = 0

  def nextElevatorUsingRoundRobin(): Int = {
    // println(s"[ElevatorControlSystemFCFS] finding the closest elevator ...")
    lastElevator += 1
    if (lastElevator > numberOfElevators) lastElevator = 1
    lastElevator
  }

  def findNextStop(stopsRequested: Set[Int], currentFloor: Int, direction: Int): Int
}
