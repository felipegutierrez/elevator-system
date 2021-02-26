package org.github.felipegutierrez.elevatorsystem.services

import org.github.felipegutierrez.elevatorsystem.actors.exceptions.ElevatorControlSystemException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.Direction
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

  "the elevator control system with First-Come-First-Serve logic with not requests" should
    "not crash" in {
    val control = new ElevatorControlSystemFCFS(10, 1)

    val nextStops01 = Queue()
    assertResult(-1)(control.findNextStop(nextStops01))
  }

  "the elevator control system with First-Come-First-Serve of a building with X floors" should
    "not allow stop requests on non existing floors" in {
    assertThrows[ElevatorControlSystemException] {
      val control = new ElevatorControlSystemFCFS(10, 1)

      val nextStops = Queue(176)
      control.findNextStop(nextStops, 6, Direction(+1))
    }
  }
}
