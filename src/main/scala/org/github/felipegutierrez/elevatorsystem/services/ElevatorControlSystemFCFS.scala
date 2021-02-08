package org.github.felipegutierrez.elevatorsystem.services

import org.github.felipegutierrez.elevatorsystem.actors.exceptions.ElevatorControlSystemException

class ElevatorControlSystemFCFS extends ElevatorControlSystem {

  def findElevator(pickUpFloor: Int, direction: Int): Int = {
    if (direction != +1 && direction != -1) throw ElevatorControlSystemException("the directions that this elevators supports are only: up [+1] and down [-1]")
    if (pickUpFloor == 1 && direction == -1) throw ElevatorControlSystemException("you cannot go down because you are on the first floor.")
    if (pickUpFloor == 16 && direction == +1) throw ElevatorControlSystemException("you cannot go up because you are on the last floor.")
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
