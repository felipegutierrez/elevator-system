package org.github.felipegutierrez.elevatorsystem.actors.util

import org.scalatest.flatspec.AnyFlatSpec

class BuildingUtilSpec extends AnyFlatSpec {

  "the building util to generate random floors on the ground floor" should
    "throws exception on wrong directions" in {
    assertThrows[RuntimeException] {
      val numberOfFloors: Int = 10
      val currentFloor: Int = 0
      val direction: Int = -1
      BuildingUtil.generateRandomFloor(numberOfFloors, currentFloor, direction)
    }
  }

  "the building util to generate random floors on the last floor" should
    "throws exception on wrong directions" in {
    assertThrows[RuntimeException] {
      val numberOfFloors: Int = 10
      val currentFloor: Int = 10
      val direction: Int = +1
      BuildingUtil.generateRandomFloor(numberOfFloors, currentFloor, direction)
    }
  }
}
