package org.github.felipegutierrez.elevatorsystem.services

import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.immutable.Queue

class ElevatorControlSystemScanSpec extends AnyFlatSpec {

  "the elevator control system with SCAN logic going UP" should
    "return the closes floor regardless the order" in {
    val control = new ElevatorControlSystemScan(10, 1)

    val nextStops01 = Queue(176, 79, 34, 60, 92, 11, 41, 114)
    assertResult(41)(control.findNextStop(nextStops01, 50, -1))
    assertResult(176)(control.findNextStop(nextStops01, 200, -1))

    val nextStops02 = Queue(79, 34, 150, 60, 92, 11, 45, 114)
    assertResult(45)(control.findNextStop(nextStops02, 50, -1))
    assertResult(150)(control.findNextStop(nextStops02, 200, -1))
  }

  "the elevator control system with SCAN logic going DOWN" should
    "return the closes floor regardless the order" in {
    val control = new ElevatorControlSystemScan(10, 1)

    val nextStops01 = Queue(176, 79, 34, 60, 92, 11, 41, 114)
    assertResult(60)(control.findNextStop(nextStops01, 50, +1))
    assertResult(34)(control.findNextStop(nextStops01, 30, +1))

    val nextStops02 = Queue(79, 34, 150, 60, 92, 11, 45, 114)
    assertResult(60)(control.findNextStop(nextStops02, 50, +1))
    assertResult(11)(control.findNextStop(nextStops02, 9, +1))
  }
}
