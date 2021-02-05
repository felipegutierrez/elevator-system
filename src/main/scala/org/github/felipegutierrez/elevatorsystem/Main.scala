package org.github.felipegutierrez.elevatorsystem

import akka.actor.{ActorSystem, Props}
import org.github.felipegutierrez.elevatorsystem.actors.{Building, Elevator, ElevatorPanel}
import org.github.felipegutierrez.elevatorsystem.controllers.ElevatorControlFCFS
import org.github.felipegutierrez.elevatorsystem.states.{BuildingState, ElevatorState}

import scala.util.Random

/**
 * This is a control system for elevators.
 */
object Main {
  def main(args: Array[String]): Unit = {
    println(s"\nThis is a control system for elevators")
    println(s"Follow the steps to create a building with elevators")
    println(s"How many floors? ")
    val numberOfFloors = 10 //scala.io.StdIn.readInt()
    println(s"How many elevators? [1-16]")
    val numberOfElevators = 3 //scala.io.StdIn.readInt()

    // construct the building with elevators and the elevator control system
    val buildingState = BuildingState(1, numberOfFloors)
    val elevators: Seq[Elevator] = (1 to numberOfElevators).map { i =>
      val currentFloor = Random.nextInt(numberOfFloors)
      val targetFloor = currentFloor
      val elevatorState = ElevatorState(i, currentFloor, targetFloor, Array[Int]())
      Elevator(elevatorState)
    }
    val elevatorControlSystem = ElevatorControlFCFS

    import org.github.felipegutierrez.elevatorsystem.actors.messages.Messages._
    val actorSystem = ActorSystem("BuildingSystem")
    val elevatorPanelActor = actorSystem.actorOf(Props[ElevatorPanel], "elevatorPanelActor")
    val buildingActor = actorSystem.actorOf(Building.props(buildingState, elevators, elevatorControlSystem), "buildingActor")

    elevatorPanelActor ! RequestPickUp(buildingActor, 4, +1)
    elevatorPanelActor ! RequestPickUp(buildingActor, 3, -1)
    elevatorPanelActor ! RequestPickUp(buildingActor, 6, +1)
  }
}
