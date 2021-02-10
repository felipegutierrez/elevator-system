package org.github.felipegutierrez.elevatorsystem

import akka.actor.{ActorSystem, Props}
import org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorPanelProtocol
import org.github.felipegutierrez.elevatorsystem.actors.util.BuildingUtil
import org.github.felipegutierrez.elevatorsystem.actors.{BuildingCoordinator, Panel}
import org.github.felipegutierrez.elevatorsystem.services.{ElevatorControlSystem, ElevatorControlSystemFCFS, ElevatorControlSystemScan}

/**
 * This is the main class of the project. It is the entry point to simulate the elevator system.
 * The systems is composed of three actors named [[org.github.felipegutierrez.elevatorsystem.actors.Panel]],
 * [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]], and [[org.github.felipegutierrez.elevatorsystem.actors.Elevator]]s.
 * The [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]] has one [[org.github.felipegutierrez.elevatorsystem.services.ElevatorControlSystem]]
 * that implements a First-Come-First-Served logic at [[org.github.felipegutierrez.elevatorsystem.services.ElevatorControlSystemFCFS]].
 * The communication among the actors are done using messages on the [[org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorPanelProtocol]].
 *
 */
object Main {
  def main(args: Array[String]): Unit = {
    println(s"\nThis is a control system for elevators")
    println(s"To help you we have some pre-built simulations")
    println(s"")
    println(s"Options using the first-come-first-serve logic for pickUp requests: ")
    println(s"1 - Building with 10 floors and 1 elevator and 4 pickUp requests: PickUp(4, +1), PickUp(1, +1), PickUp(10, -1), PickUp(7, -1)")
    println(s"2 - Building with 10 floors and 2 elevators and 4 pickUp requests: PickUp(4, +1), PickUp(1, +1), PickUp(10, -1), PickUp(7, -1)")
    println(s"3 - Building with 100 floors and 1 elevator and 40 random pickUp requests")
    println(s"4 - Building with 100 floors and 10 elevators and 40 random pickUp requests")
    println(s"")
    println(s"Options using the SCAN logic for pickUp requests: ")
    println(s"5 - Building with 10 floors and 1 elevator and 4 pickUp requests: PickUp(4, +1), PickUp(1, +1), PickUp(10, -1), PickUp(7, -1)")
    println(s"6 - Building with 10 floors and 2 elevators and 4 pickUp requests: PickUp(4, +1), PickUp(1, +1), PickUp(10, -1), PickUp(7, -1)")
    println(s"7 - Building with 100 floors and 1 elevator and 40 random pickUp requests")
    println(s"8 - Building with 100 floors and 10 elevators and 40 random pickUp requests")
    println(s"")
    print(s"Choose your option: ")

    val option01: Int = scala.io.StdIn.readInt()
    option01 match {
      // cases for the First-Come-First-Serve controller
      case 1 =>
        // testing system with 1 elevator and the First-Come-First-Serve controller
        val numberOfFloors = 10
        val numberOfElevators = 1
        run(numberOfFloors, numberOfElevators, new ElevatorControlSystemFCFS(numberOfFloors, numberOfElevators))
      case 2 =>
        // testing system with 2 elevators and the First-Come-First-Serve controller
        val numberOfFloors = 10
        val numberOfElevators = 2
        run(numberOfFloors, numberOfElevators, new ElevatorControlSystemFCFS(numberOfFloors, numberOfElevators))
      case 3 =>
        // testing system of a skyscraper building with 1 elevator and the First-Come-First-Serve controller
        val numberOfFloors = 100
        val numberOfElevators = 1
        val numberOfRandomPickUps = 40
        run(numberOfFloors, numberOfElevators, new ElevatorControlSystemFCFS(numberOfFloors, numberOfElevators), numberOfRandomPickUps)
      case 4 =>
        // testing system of a skyscraper building with 10 elevators and the First-Come-First-Serve controller
        val numberOfFloors = 100
        val numberOfElevators = 10
        val numberOfRandomPickUps = 40
        run(numberOfFloors, numberOfElevators, new ElevatorControlSystemFCFS(numberOfFloors, numberOfElevators), numberOfRandomPickUps)
      // cases for the SCAN controller
      case 5 =>
        // testing system with 1 elevator and the SCAN controller
        val numberOfFloors = 10
        val numberOfElevators = 1
        run(numberOfFloors, numberOfElevators, new ElevatorControlSystemScan(numberOfFloors, numberOfElevators))
      case 6 =>
        // testing system with 2 elevators and the SCAN controller
        val numberOfFloors = 10
        val numberOfElevators = 2
        run(numberOfFloors, numberOfElevators, new ElevatorControlSystemScan(numberOfFloors, numberOfElevators))
      case 7 =>
        // testing system of a skyscraper building with 1 elevator and the SCAN controller
        val numberOfFloors = 100
        val numberOfElevators = 1
        val numberOfRandomPickUps = 40
        run(numberOfFloors, numberOfElevators, new ElevatorControlSystemScan(numberOfFloors, numberOfElevators), numberOfRandomPickUps)
      case 8 =>
        // testing system of a skyscraper building with 10 elevators and the SCAN controller
        val numberOfFloors = 100
        val numberOfElevators = 10
        val numberOfRandomPickUps = 40
        run(numberOfFloors, numberOfElevators, new ElevatorControlSystemScan(numberOfFloors, numberOfElevators), numberOfRandomPickUps)
      case _ => println(s"unavailable option")
    }
  }

  def run(numberOfFloors: Int, numberOfElevators: Int, controller: ElevatorControlSystem, numberOfRandomPickUps: Int): Unit = {

    val system = ActorSystem("ElevatorSystem")
    val panelActor = system.actorOf(Props[Panel], "panelActor")
    val buildingCoordinatorActorName = "buildingCoordinatorActor"
    val buildingCoordinatorActor = system.actorOf(
      BuildingCoordinator.props(buildingCoordinatorActorName, numberOfFloors, numberOfElevators, controller),
      buildingCoordinatorActorName)

    for (i <- 0 until numberOfRandomPickUps) {

      val randomFloor = BuildingUtil.generateRandomFloor(numberOfFloors, i, if (i % 2 == 0) 1 else -1)
      val randomDirection = {
        if (randomFloor < 5) +1
        else if (randomFloor > numberOfFloors - 5) -1
        else {
          if (randomFloor % 2 == 0) 1 else -1
        }
      }
      panelActor ! ElevatorPanelProtocol.PickUp(randomFloor, randomDirection, buildingCoordinatorActor)
    }
  }

  def run(numberOfFloors: Int, numberOfElevators: Int, controller: ElevatorControlSystem): Unit = {

    val system = ActorSystem("ElevatorSystem")
    val panelActor = system.actorOf(Props[Panel], "panelActor")
    val buildingCoordinatorActorName = "buildingCoordinatorActor"
    val buildingCoordinatorActor = system.actorOf(
      BuildingCoordinator.props(buildingCoordinatorActorName, numberOfFloors, numberOfElevators, controller),
      buildingCoordinatorActorName)

    panelActor ! ElevatorPanelProtocol.PickUp(4, +1, buildingCoordinatorActor)
    panelActor ! ElevatorPanelProtocol.PickUp(1, +1, buildingCoordinatorActor)
    panelActor ! ElevatorPanelProtocol.PickUp(10, -1, buildingCoordinatorActor)
    panelActor ! ElevatorPanelProtocol.PickUp(7, -1, buildingCoordinatorActor)
  }
}
