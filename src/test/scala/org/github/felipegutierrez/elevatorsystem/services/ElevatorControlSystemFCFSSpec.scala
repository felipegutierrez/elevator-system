package org.github.felipegutierrez.elevatorsystem.services

import org.github.felipegutierrez.elevatorsystem.actors.exceptions.ElevatorControlSystemException
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class ElevatorControlSystemFCFSSpec extends AnyFlatSpec {

  if (Runtime.getRuntime().availableProcessors() >= 4) {
    "the elevator control system" should
      "return an elevator ID when requested within 5 seconds" in {
      val control: ElevatorControlSystem = new ElevatorControlSystemFCFS()
      val elevatorId: Future[Int] = control.findElevator(1, +1)

      Await.result(elevatorId, 5 seconds)
      assert(elevatorId.isCompleted)
    }
  }
  "the elevator control system with wrong directions" should
    "not allow movements" in {
    val control: ElevatorControlSystem = new ElevatorControlSystemFCFS()
    try {
      control.findElevator(1, +2)
    } catch {
      case ElevatorControlSystemException(msg) =>
        println(msg)
        assert(true)
      case _: Throwable => fail("we should get an exception here")
    }
    try {
      control.findElevator(1, -2)
    } catch {
      case ElevatorControlSystemException(msg) =>
        println(msg)
        assert(true)
      case _: Throwable => fail("we should get an exception here")
    }
  }
  "the elevator control system with on the top of the building" should
    "not allow movements" in {
    val control: ElevatorControlSystem = new ElevatorControlSystemFCFS()
    try {
      control.findElevator(16, +1)
    } catch {
      case ElevatorControlSystemException(msg) =>
        println(msg)
        assert(true)
      case _: Throwable => fail("we should get an exception here")
    }
  }
  "the elevator control system with on the bottom of the building" should
    "not allow movements" in {
    val control: ElevatorControlSystem = new ElevatorControlSystemFCFS()
    try {
      control.findElevator(1, -1)
    } catch {
      case ElevatorControlSystemException(msg) =>
        println(msg)
        assert(true)
      case _: Throwable => fail("we should get an exception here")
    }
  }
}
