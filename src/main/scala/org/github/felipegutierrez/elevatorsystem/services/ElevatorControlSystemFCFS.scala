package org.github.felipegutierrez.elevatorsystem.services

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}

class ElevatorControlSystemFCFS extends ElevatorControlSystem {

  private implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))

  def findElevator(pickUpFloor: Int, direction: Int): Future[Int] = {
    println(s"[ElevatorControlSystemFCFS] finding the closest elevator ...")
    val elevatorId = 1
    Future(elevatorId)
  }
}
