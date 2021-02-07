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

    elevatorPanelActor ! PickUp(buildingActor)

    Thread.sleep(5000)
    system.terminate()
  }
}
