package org.github.felipegutierrez.elevatorsystem.services

import org.github.felipegutierrez.elevatorsystem.actors.exceptions.ElevatorControlSystemException

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}

class ElevatorControlSystemFCFS extends ElevatorControlSystem {

  private implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))

  def findElevator(pickUpFloor: Int, direction: Int): Future[Int] = {
    if (direction != +1 && direction != -1) throw ElevatorControlSystemException("the directions that this elevators supports are only: up [+1] and down [-1]")
    if (pickUpFloor == 1 && direction == -1) throw ElevatorControlSystemException("you cannot go down because you are on the first floor.")
    if (pickUpFloor == 16 && direction == +1) throw ElevatorControlSystemException("you cannot go up because you are on the last floor.")
    println(s"[ElevatorControlSystemFCFS] finding the closest elevator ...")
    val elevatorId = 1
    Future(elevatorId)
  }
}
