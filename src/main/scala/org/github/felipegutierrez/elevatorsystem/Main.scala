package org.github.felipegutierrez.elevatorsystem

import akka.actor.{ActorSystem, Props}
import org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol._
import org.github.felipegutierrez.elevatorsystem.actors.{BuildingCoordinator, Panel}

/**
 * This is the main class of the project. It is the entry point to simulate the elevator system.
 * The systems is composed of three actors named [[org.github.felipegutierrez.elevatorsystem.actors.Panel]],
 * [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]], and [[org.github.felipegutierrez.elevatorsystem.actors.Elevator]]s.
 * The [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]] has one [[org.github.felipegutierrez.elevatorsystem.services.ElevatorControlSystem]]
 * that implements a First-Come-First-Served logic at [[org.github.felipegutierrez.elevatorsystem.services.ElevatorControlSystemFCFS]].
 * The communication among the actors are done using messages on the [[org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol]].
 *
 */
object Main {
  def main(args: Array[String]): Unit = {
    println(s"\nThis is a control system for elevators")

    val numberOfFloors: Int = 10
    val numberOfElevators: Int = 1

    val system = ActorSystem("ElevatorSystem")
    val panelActor = system.actorOf(Props[Panel], "panelActor")
    val buildingCoordinatorActorName = "buildingCoordinatorActor"
    val buildingCoordinatorActor = system.actorOf(BuildingCoordinator.props(buildingCoordinatorActorName, numberOfFloors, numberOfElevators), buildingCoordinatorActorName)

    panelActor ! PickUp(4, +1, buildingCoordinatorActor)
    panelActor ! PickUp(1, +1, buildingCoordinatorActor)
    panelActor ! PickUp(10, -1, buildingCoordinatorActor)
    panelActor ! PickUp(7, -1, buildingCoordinatorActor)

  }
}
