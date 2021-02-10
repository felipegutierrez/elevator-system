package org.github.felipegutierrez.elevatorsystem.services

import org.scalatest.flatspec.AnyFlatSpec

class ElevatorControlSystemSpec extends AnyFlatSpec {

  "the elevator control system with one elevator" should
    "return only the same elevator" in {
    val control = new ElevatorControlSystemFCFS(10, 1)
    assertResult(1)(control.nextElevatorUsingRoundRobin())
    assertResult(1)(control.nextElevatorUsingRoundRobin())
    assertResult(1)(control.nextElevatorUsingRoundRobin())
  }
  "the elevator control system with two elevators" should
    "return the elevators id in round robin fashion" in {
    val control = new ElevatorControlSystemFCFS(10, 2)
    assertResult(1)(control.nextElevatorUsingRoundRobin())
    assertResult(2)(control.nextElevatorUsingRoundRobin())
    assertResult(1)(control.nextElevatorUsingRoundRobin())
    assertResult(2)(control.nextElevatorUsingRoundRobin())
    assertResult(1)(control.nextElevatorUsingRoundRobin())
  }
  "the elevator control system with three elevators" should
    "return the elevators id in round robin fashion" in {
    val control = new ElevatorControlSystemFCFS(10, 3)
    assertResult(1)(control.nextElevatorUsingRoundRobin())
    assertResult(2)(control.nextElevatorUsingRoundRobin())
    assertResult(3)(control.nextElevatorUsingRoundRobin())
    assertResult(1)(control.nextElevatorUsingRoundRobin())
    assertResult(2)(control.nextElevatorUsingRoundRobin())
    assertResult(3)(control.nextElevatorUsingRoundRobin())
  }
}
