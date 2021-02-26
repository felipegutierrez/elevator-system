package org.github.felipegutierrez.elevatorsystem.actors.protocol

import org.github.felipegutierrez.elevatorsystem.actors.exceptions.ElevatorControlSystemException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.Direction
import org.scalatest.flatspec.AnyFlatSpec

class BuildingCoordinatorProtocolSpec extends AnyFlatSpec {

  "a building that receives Direction messages with existing direction" should
    "accept the direction" in {
    val direction00: Direction = Direction(0)
    assertResult(Direction(0))(direction00)

    val direction01: Direction = Direction(+1)
    assertResult(Direction(+1))(direction01)

    val direction02: Direction = Direction(-1)
    assertResult(Direction(-1))(direction02)
  }

  "a building that receives Direction messages with wrong direction" should
    "throws exception on wrong directions" in {
    assertThrows[ElevatorControlSystemException] {
      val direction: Direction = Direction(-2)
    }
    assertThrows[ElevatorControlSystemException] {
      val direction: Direction = Direction(+2)
    }
  }
}
