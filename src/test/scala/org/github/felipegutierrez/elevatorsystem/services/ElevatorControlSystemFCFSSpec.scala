package org.github.felipegutierrez.elevatorsystem.services

import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.immutable.Queue

class ElevatorControlSystemFCFSSpec extends AnyFlatSpec {

  "the elevator control system with First-Come-First-Serve logic" should
    "return floors in order of insert" in {
    val control = new ElevatorControlSystemFCFS(10, 1)

    val nextStops01 = Queue(2, 6, 8, 4)
    assertResult(2)(control.findNextStop(nextStops01))
    val nextStops02 = nextStops01.enqueue(10)
    assertResult(2)(control.findNextStop(nextStops02))
    val nextStops03 = nextStops01.dequeue._2
    assertResult(6)(control.findNextStop(nextStops03))
  }
}
