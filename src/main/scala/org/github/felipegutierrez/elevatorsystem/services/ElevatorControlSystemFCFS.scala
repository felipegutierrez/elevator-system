package org.github.felipegutierrez.elevatorsystem.services

class ElevatorControlSystemFCFS extends ElevatorControlSystem {

  def findElevator(pickUpFloor: Int, direction: Int): Int = {
    println(s"[ElevatorControlSystemFCFS] finding the closest elevator ...")
    // TODO: for now we have only one elevator
    1
  }

  override def findNextStop(stopsRequested: Set[Int], currentFloor: Int, direction: Int): Int = {
    findNextStop(stopsRequested)
  }

  override def findNextStop(stopsRequested: Set[Int]): Int = {
    // val v = stopsRequested.map(x => s"$x , ").mkString
    // println(s"[ElevatorControlSystemFCFS] next stop from list: $v")
    stopsRequested.head
  }
}
