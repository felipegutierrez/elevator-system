package org.github.felipegutierrez.elevatorsystem.services

import org.github.felipegutierrez.elevatorsystem.actors.exceptions.ElevatorControlSystemException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.Direction
import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.immutable.Queue

class ElevatorControlSystemScanSpec extends AnyFlatSpec {

  "the elevator control system with SCAN logic going UP" should
    "return the closes floor regardless the order" in {
    val control = new ElevatorControlSystemScan(180, 1)

    val nextStops01 = Queue(176, 79, 34, 60, 92, 11, 41, 114)
    assertResult(Some(41))(control.findNextStop(nextStops01, 50, Direction(-1)))
    assertResult(Some(176))(control.findNextStop(nextStops01, 200, Direction(-1)))

    val nextStops02 = Queue(79, 34, 150, 60, 92, 11, 45, 114)
    assertResult(Some(45))(control.findNextStop(nextStops02, 50, Direction(-1)))
    assertResult(Some(150))(control.findNextStop(nextStops02, 200, Direction(-1)))
  }

  "the elevator control system with SCAN logic going DOWN" should
    "return the closes floor regardless the order" in {
    val control = new ElevatorControlSystemScan(180, 1)

    val nextStops01 = Queue(176, 79, 34, 60, 92, 11, 41, 114)
    assertResult(Some(60))(control.findNextStop(nextStops01, 50, Direction(+1)))
    assertResult(Some(34))(control.findNextStop(nextStops01, 30, Direction(+1)))

    val nextStops02 = Queue(79, 34, 150, 60, 92, 11, 45, 114)
    assertResult(Some(60))(control.findNextStop(nextStops02, 50, Direction(+1)))
    assertResult(Some(11))(control.findNextStop(nextStops02, 9, Direction(+1)))
  }

  "the elevator control system with SCAN that receives an empty list" should
    "not return a valid stop" in {
    val control = new ElevatorControlSystemScan(10, 1)

    val nextStops01 = Queue()
    assertResult(None)(control.findNextStop(nextStops01, 5, Direction(-1)))
  }

  "the elevator control system with SCAN that receives a list of stops with one floor and the current floor is higher than that floor" should
    "return " in {
    val control = new ElevatorControlSystemScan(10, 1)

    val nextStops = Queue(5)
    assertResult(None)(control.findNextStop(nextStops, 6, Direction(+1)))
    assertResult(None)(control.findNextStop(nextStops, 3, Direction(-1)))
  }

  "the elevator control system with SCAN of a building with X floors" should
    "not allow stop requests on non existing floors" in {
    assertThrows[ElevatorControlSystemException] {
      val control = new ElevatorControlSystemScan(10, 1)

      val nextStops = Queue(176, 79, 34, 60, 92, 11, 41, 114)
      assertResult(None)(control.findNextStop(nextStops, 6, Direction(+1)))
    }
  }
}
