package org.github.felipegutierrez.elevatorsystem.services

class ElevatorControlSystemFCFS(val numberOfElevators: Int) extends ElevatorControlSystem {

  var lastElevator = 0

  def nextElevatorUsingRoundRobin(): Int = {
    // println(s"[ElevatorControlSystemFCFS] finding the closest elevator ...")
    lastElevator += 1
    if (lastElevator > numberOfElevators) lastElevator = 1
    lastElevator
  }

  override def findNextStop(stopsRequested: Set[Int], currentFloor: Int, direction: Int): Int = {
    findNextStop(stopsRequested)
  }

  override def findNextStop(stopsRequested: Set[Int]): Int = {
    // println(s"[ElevatorControlSystemFCFS] next stop from list: ${stopsRequested.map(x => s"$x , ").mkString}")
    stopsRequested.head
  }
}
