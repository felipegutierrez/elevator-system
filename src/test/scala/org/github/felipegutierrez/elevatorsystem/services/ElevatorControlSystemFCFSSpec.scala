package org.github.felipegutierrez.elevatorsystem.services

import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.mutable

class ElevatorControlSystemFCFSSpec extends AnyFlatSpec {

  "the elevator control system with First-Come-First-Serve logic" should
    "return floors in order of insert" in {
    val control = new ElevatorControlSystemFCFS(10, 1)

    val nextStops01 = mutable.Queue(2, 6, 8, 4)
    assertResult(2)(control.findNextStop(nextStops01))
    nextStops01.enqueue(10)
    assertResult(2)(control.findNextStop(nextStops01))
    nextStops01.dequeue()
    assertResult(6)(control.findNextStop(nextStops01))
  }
}
