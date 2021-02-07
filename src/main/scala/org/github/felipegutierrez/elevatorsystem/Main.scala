package org.github.felipegutierrez.elevatorsystem

import akka.actor.{ActorSystem, Props}
import org.github.felipegutierrez.elevatorsystem.actors.{Building, ElevatorPanel}
import org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol._

object Main {
  def main(args: Array[String]): Unit = {
    println(s"\nThis is a control system for elevators")

    val numberOfFloors: Int = 10
    val numberOfElevators: Int = 1

    val system = ActorSystem("ElevatorSystem")
    val elevatorPanelActor = system.actorOf(Props[ElevatorPanel], "elevatorPanelActor")
    val buildingActor = system.actorOf(Building.props(numberOfFloors, numberOfElevators), "buildingActor")

    elevatorPanelActor ! PickUp(4, +1, buildingActor)
    // elevatorPanelActor ! PickUp(1, +1, buildingActor)
    // elevatorPanelActor ! PickUp(10, -1, buildingActor)
    // elevatorPanelActor ! PickUp(7, -1, buildingActor)

    Thread.sleep(10000)
  }
}
