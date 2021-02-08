package org.github.felipegutierrez.elevatorsystem.services

import org.github.felipegutierrez.elevatorsystem.actors.exceptions.ElevatorControlSystemException
import org.scalatest.flatspec.AnyFlatSpec

class ElevatorControlSystemFCFSSpec extends AnyFlatSpec {

  "the elevator control system" should
    "return an elevator ID when requested within 5 seconds" in {
    val control: ElevatorControlSystem = new ElevatorControlSystemFCFS()
    val elevatorId: Int = control.findElevator(1, +1)

    assertResult(1)(elevatorId)
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
  "the elevator control system with First-Come-First-Serve logic" should
    "return floors in order of insert" in {
    val control: ElevatorControlSystem = new ElevatorControlSystemFCFS()

    val nextStops01 = Set(2, 6, 8, 4)
    assertResult(2)(control.findNextStop(nextStops01))

    val nextStops02 = Set(6, 8, 4)
    assertResult(6)(control.findNextStop(nextStops02))
  }
}
