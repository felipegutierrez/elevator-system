package org.github.felipegutierrez.elevatorsystem.actors.util

import org.scalatest.flatspec.AnyFlatSpec

class BuildingUtilSpec extends AnyFlatSpec {

  "the building util to generate random floors on the ground floor" should
    "throws exception on wrong directions" in {
    val numberOfFloors: Int = 10
    val currentFloor: Int = 0
    val direction: Int = -1
    try {
      val result = BuildingUtil.generateRandomFloor(numberOfFloors, currentFloor, direction)
    } catch {
      case _: IllegalArgumentException => assert(true)
      case _: RuntimeException => assert(true)
      case _: Throwable => fail("we except an Exception here")
    }
  }

  "the building util to generate random floors on the last floor" should
    "throws exception on wrong directions" in {
    val numberOfFloors: Int = 10
    val currentFloor: Int = 10
    val direction: Int = +1
    try {
      val result = BuildingUtil.generateRandomFloor(numberOfFloors, currentFloor, direction)
      println(result)
    } catch {
      case _: IllegalArgumentException => assert(true)
      case _: RuntimeException => assert(true)
      case _: Throwable => fail("we except an Exception here")
    }
  }
}
