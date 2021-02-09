package org.github.felipegutierrez.elevatorsystem.services

import org.scalatest.flatspec.AnyFlatSpec

class ElevatorControlSystemFCFSSpec extends AnyFlatSpec {

  "the elevator control system with one elevator" should
    "return only the same elevator" in {
    val control: ElevatorControlSystem = new ElevatorControlSystemFCFS(1)
    assertResult(1)(control.nextElevatorUsingRoundRobin())
    assertResult(1)(control.nextElevatorUsingRoundRobin())
    assertResult(1)(control.nextElevatorUsingRoundRobin())
  }
  "the elevator control system with two elevators" should
    "return the elevators id in round robin fashion" in {
    val control: ElevatorControlSystem = new ElevatorControlSystemFCFS(2)
    assertResult(1)(control.nextElevatorUsingRoundRobin())
    assertResult(2)(control.nextElevatorUsingRoundRobin())
    assertResult(1)(control.nextElevatorUsingRoundRobin())
    assertResult(2)(control.nextElevatorUsingRoundRobin())
    assertResult(1)(control.nextElevatorUsingRoundRobin())
  }
  "the elevator control system with three elevators" should
    "return the elevators id in round robin fashion" in {
    val control: ElevatorControlSystem = new ElevatorControlSystemFCFS(3)
    assertResult(1)(control.nextElevatorUsingRoundRobin())
    assertResult(2)(control.nextElevatorUsingRoundRobin())
    assertResult(3)(control.nextElevatorUsingRoundRobin())
    assertResult(1)(control.nextElevatorUsingRoundRobin())
    assertResult(2)(control.nextElevatorUsingRoundRobin())
    assertResult(3)(control.nextElevatorUsingRoundRobin())
  }
  "the elevator control system with First-Come-First-Serve logic" should
    "return floors in order of insert" in {
    val control: ElevatorControlSystem = new ElevatorControlSystemFCFS(1)

    val nextStops01 = Set(2, 6, 8, 4)
    assertResult(2)(control.findNextStop(nextStops01))

    val nextStops02 = Set(6, 8, 4)
    assertResult(6)(control.findNextStop(nextStops02))
  }
}
