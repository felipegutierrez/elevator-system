package org.github.felipegutierrez.elevatorsystem.services

trait ElevatorControlSystem {
  def nextElevatorUsingRoundRobin(): Int

  def findNextStop(stopsRequested: Set[Int]): Int

  def findNextStop(stopsRequested: Set[Int], currentFloor: Int, direction: Int): Int
}
