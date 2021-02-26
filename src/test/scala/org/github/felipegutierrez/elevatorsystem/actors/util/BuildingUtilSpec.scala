package org.github.felipegutierrez.elevatorsystem.actors.util

import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.Direction
import org.scalatest.flatspec.AnyFlatSpec

class BuildingUtilSpec extends AnyFlatSpec {

  "the building util to generate random floors on the ground floor" should
    "throws exception on wrong directions" in {
    assertThrows[RuntimeException] {
      val numberOfFloors: Int = 10
      val currentFloor: Int = 0
      val direction: Direction = Direction(-1)
      BuildingUtil.generateRandomFloor(numberOfFloors, currentFloor, direction)
    }
  }

  "the building util to generate random floors on the last floor" should
    "throws exception on wrong directions" in {
    assertThrows[RuntimeException] {
      val numberOfFloors: Int = 10
      val currentFloor: Int = 10
      val direction: Direction = Direction(+1)
      BuildingUtil.generateRandomFloor(numberOfFloors, currentFloor, direction)
    }
  }

  "the building util to generate random floors when going UP" should
    "generate a floor higher than the current floor" in {
    val numberOfFloors: Int = 10
    val currentFloor: Int = 5
    val direction: Direction = Direction(+1)
    val generatedFloor = BuildingUtil.generateRandomFloor(numberOfFloors, currentFloor, direction)
    assert(generatedFloor > currentFloor)
    assert(generatedFloor <= numberOfFloors)
  }

  "the building util to generate random floors when going DOWN" should
    "generate a floor lower than the current floor" in {
    val numberOfFloors: Int = 10
    val currentFloor: Int = 5
    val direction: Direction = Direction(-1)
    val generatedFloor = BuildingUtil.generateRandomFloor(numberOfFloors, currentFloor, direction)
    assert(generatedFloor >= 0)
    assert(generatedFloor < currentFloor)
  }

  "a skyscraper building util to generate random floors when going UP" should
    "generate a floor higher than the current floor" in {
    val numberOfFloors: Int = 100
    val currentFloor: Int = 5
    val direction: Direction = Direction(+1)
    val generatedFloor = BuildingUtil.generateRandomFloor(numberOfFloors, currentFloor, direction)
    assert(generatedFloor > currentFloor)
    assert(generatedFloor <= numberOfFloors)
  }

  "a skyscraper building util to generate random floors when going DOWN" should
    "generate a floor lower than the current floor" in {
    val numberOfFloors: Int = 100
    val currentFloor: Int = 95
    val direction: Direction = Direction(-1)
    val generatedFloor = BuildingUtil.generateRandomFloor(numberOfFloors, currentFloor, direction)
    assert(generatedFloor >= 0)
    assert(generatedFloor < currentFloor)
  }
}
