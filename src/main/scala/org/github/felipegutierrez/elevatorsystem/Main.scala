package org.github.felipegutierrez.elevatorsystem

import akka.actor.{ActorSystem, Props}
import org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorPanelProtocol
import org.github.felipegutierrez.elevatorsystem.actors.util.BuildingUtil
import org.github.felipegutierrez.elevatorsystem.actors.{BuildingCoordinator, Panel}
import org.github.felipegutierrez.elevatorsystem.services.ElevatorControlSystem

/**
 * This is the main class of the project. It is the entry point to simulate the elevator system.
 * The systems is composed of three actors named [[org.github.felipegutierrez.elevatorsystem.actors.Panel]],
 * [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]], and [[org.github.felipegutierrez.elevatorsystem.actors.Elevator]]s.
 * The [[org.github.felipegutierrez.elevatorsystem.actors.BuildingCoordinator]] is the dispatcher that coordinates how the elevators move on the building.
 * To accomplish this task uses one [[org.github.felipegutierrez.elevatorsystem.services.ElevatorControlSystem]]
 * which can be a controller that implements the [[https://en.wikipedia.org/wiki/FIFO_(computing_and_electronics) FCFS algorithm]] at [[org.github.felipegutierrez.elevatorsystem.services.ElevatorControlSystemFCFS]]
 * or a controller that implements the [[https://en.wikipedia.org/wiki/Elevator_algorithm SCAN algorithm]] at [[org.github.felipegutierrez.elevatorsystem.services.ElevatorControlSystemScan]].
 * The communication among the actors are done using messages on the [[org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorPanelProtocol]],
 * the [[org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol]], and the [[org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorProtocol]].
 *
 */
object Main {
  def main(args: Array[String]): Unit = {
    println("\nThis is a control system for elevators")
    println("To help you we have some pre-built simulations\n")
    println("Options using the first-come-first-serve logic for pickUp requests: ")
    println("1 - Building with 10 floors and 1 elevator and 4 pickUp requests: PickUp(4, +1), PickUp(1, +1), PickUp(10, -1), PickUp(7, -1)")
    println("2 - Building with 10 floors and 2 elevators and 4 pickUp requests: PickUp(4, +1), PickUp(1, +1), PickUp(10, -1), PickUp(7, -1)")
    println("3 - Building with 100 floors and 1 elevator and 40 random pickUp requests")
    println("4 - Building with 100 floors and 10 elevators and 40 random pickUp requests\n")
    println("Options using the SCAN logic for pickUp requests: ")
    println("5 - Building with 10 floors and 1 elevator and 4 pickUp requests: PickUp(4, +1), PickUp(1, +1), PickUp(10, -1), PickUp(7, -1)")
    println("6 - Building with 10 floors and 2 elevators and 4 pickUp requests: PickUp(4, +1), PickUp(1, +1), PickUp(10, -1), PickUp(7, -1)")
    println("7 - Building with 100 floors and 1 elevator and 40 random pickUp requests")
    println("8 - Building with 100 floors and 10 elevators and 40 random pickUp requests\n")
    println("9 - Interactive start configuration\n")
    print("Choose your option: ")

    val option = scala.io.StdIn.readLine()
    option match {
      // ################## cases for the First-Come-First-Serve controller ##################
      case "1" =>
        // testing system with 1 elevator and the First-Come-First-Serve controller
        run(10, 1, ElevatorControlSystem.FCFSControlSystem)
      case "2" =>
        // testing system with 2 elevators and the First-Come-First-Serve controller
        run(10, 2, ElevatorControlSystem.FCFSControlSystem)
      case "3" =>
        // testing system of a skyscraper building with 1 elevator and the First-Come-First-Serve controller
        run(100, 1, ElevatorControlSystem.FCFSControlSystem, 40)
      case "4" =>
        // testing system of a skyscraper building with 10 elevators and the First-Come-First-Serve controller
        run(100, 10, ElevatorControlSystem.FCFSControlSystem, 40)
      // ################## cases for the SCAN controller ##################
      case "5" =>
        // testing system with 1 elevator and the SCAN controller
        run(10, 1, ElevatorControlSystem.ScanControlSystem)
      case "6" =>
        // testing system with 2 elevators and the SCAN controller
        run(10, 2, ElevatorControlSystem.ScanControlSystem)
      case "7" =>
        // testing system of a skyscraper building with 1 elevator and the SCAN controller
        run(100, 1, ElevatorControlSystem.ScanControlSystem, 40)
      case "8" =>
        // testing system of a skyscraper building with 10 elevators and the SCAN controller
        run(100, 10, ElevatorControlSystem.FCFSControlSystem, 40)
      // ################## interactive start configuration ##################
      case "9" =>
        print("Number of floors in the building [2-100]: ")
        val numberOfFloors = scala.io.StdIn.readInt()
        print("Number of elevators in the building [1-16]: ")
        val numberOfElevators = scala.io.StdIn.readInt()
        print("Elevator control system [1 - FCFS, 2 - SCAN]: ")
        val elevatorControl = scala.io.StdIn.readInt()
        print("Number of random pickups to generate: ")
        val randomPickUps = scala.io.StdIn.readInt()
        val elevatorControlSystem = elevatorControl match {
          case elevatorControl if (elevatorControl == 1) => ElevatorControlSystem.FCFSControlSystem
          case elevatorControl if (elevatorControl == 2) => ElevatorControlSystem.ScanControlSystem
          case _ => throw new RuntimeException("Wrong elevator controller system. Please select 1 - [FCFS] or 2 - [SCAN].")
        }
        run(numberOfFloors, numberOfElevators, elevatorControlSystem, randomPickUps)
      case _ => println("unavailable option")
    }
  }

  /**
   * * This method simulates the ElevatorSystem with 2 actors named panelActor and buildingCoordinatorActor.
   * It is possible to set the number of floors of the building, the number of elevators, which elevator controller
   * system the building will use to coordinate the elevators, and the number of pickUps.
   * If the numberOfRandomPickUps is set to 0, the panelActor will send four pre-defined pickUps to the building:
   * PickUp(4, +1)
   * PickUp(1, +1)
   * PickUp(10, -1)
   * PickUp(7, -1)
   * If the numberOfRandomPickUps is greater than 0 the simulation will generate the given number of random pickUps.
   *
   * @param numberOfFloors
   * @param numberOfElevators
   * @param elevatorControlSystemType
   * @param numberOfRandomPickUps
   */
  def run(numberOfFloors: Int, numberOfElevators: Int, elevatorControlSystemType: ElevatorControlSystem.ElevatorControlSystemType, numberOfRandomPickUps: Int = 0): Unit = {

    val system = ActorSystem("ElevatorSystem")
    val panelActor = system.actorOf(Props[Panel], "panelActor")
    val buildingCoordinatorActorName = "buildingCoordinatorActor"
    val buildingCoordinatorActor = system.actorOf(
      BuildingCoordinator.props(
        buildingCoordinatorActorName,
        numberOfFloors,
        numberOfElevators,
        elevatorControlSystemType).withDispatcher("building-coordinator-dispatcher"),
      buildingCoordinatorActorName)

    if (numberOfRandomPickUps <= 0) {
      // pre defined pick ups
      panelActor ! ElevatorPanelProtocol.PickUp(4, +1, buildingCoordinatorActor)
      panelActor ! ElevatorPanelProtocol.PickUp(1, +1, buildingCoordinatorActor)
      panelActor ! ElevatorPanelProtocol.PickUp(10, -1, buildingCoordinatorActor)
      panelActor ! ElevatorPanelProtocol.PickUp(7, -1, buildingCoordinatorActor)
    } else {
      // random pick ups
      for (i <- 0 until numberOfRandomPickUps) {
        val randomFloor = BuildingUtil.generateRandomFloor(numberOfFloors, i, if (i % 2 == 0) 1 else -1)
        //        val randomDirection = {
        //          if (randomFloor < 5) +1
        //          else if (randomFloor > numberOfFloors - 5) -1
        //          else {
        //            if (randomFloor % 2 == 0) 1 else -1
        //          }
        //        }
        val randomDirection = randomFloor match {
          case randomFloor if (randomFloor < 5) => +1
          case randomFloor if (randomFloor > numberOfFloors - 5) => -1
          case randomFloor if (randomFloor % 2 == 0) => +1
          case _ => -1
        }

        panelActor ! ElevatorPanelProtocol.PickUp(randomFloor, randomDirection, buildingCoordinatorActor)
      }
    }
  }
}
