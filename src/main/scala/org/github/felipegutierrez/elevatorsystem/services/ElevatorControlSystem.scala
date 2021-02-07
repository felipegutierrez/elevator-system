package org.github.felipegutierrez.elevatorsystem.services

import scala.concurrent.Future

trait ElevatorControlSystem {
  def findElevator(pickUpFloor: Int, direction: Int): Future[Int]
}
