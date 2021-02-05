package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.github.felipegutierrez.elevatorsystem.actors.messages.Messages.{RequestPickUp, ResponsePickUp}
import org.github.felipegutierrez.elevatorsystem.controllers.ElevatorControlFCFS
import org.github.felipegutierrez.elevatorsystem.states.{BuildingState, ElevatorState}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.util.Random

class ElevatorPanelSpec extends TestKit(ActorSystem("BuildingSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "The elevator panel of the building" should {
    "receive pick_up messages for a given building and respond the pick_up as well" in {

      val numberOfFloors = 10
      val numberOfElevators = 3
      val buildingState = BuildingState(1, numberOfFloors)
      val elevators: Seq[Elevator] = (1 to numberOfElevators).map { i =>
        val currentFloor = Random.nextInt(numberOfFloors)
        val targetFloor = currentFloor
        val elevatorState = ElevatorState(i, currentFloor, targetFloor, Array[Int]())
        Elevator(elevatorState)
      }
      val elevatorControlSystem = ElevatorControlFCFS

      // val elevatorPanelActor = system.actorOf(Props[ElevatorPanel], "elevatorPanelActor")
      val elevatorPanelActor = TestActorRef[ElevatorPanel](Props[ElevatorPanel])
      val buildingActor = system.actorOf(Building.props(buildingState, elevators, elevatorControlSystem), "buildingActor")
      elevatorPanelActor ! RequestPickUp(buildingActor, 4, +1)
      elevatorPanelActor ! RequestPickUp(buildingActor, 3, -1)
      elevatorPanelActor ! RequestPickUp(buildingActor, 6, +1)

      elevatorPanelActor.receive(RequestPickUp)
      elevatorPanelActor.receive(RequestPickUp)
      elevatorPanelActor.receive(RequestPickUp)

      elevatorPanelActor.receive(ResponsePickUp)
      elevatorPanelActor.receive(ResponsePickUp)
      elevatorPanelActor.receive(ResponsePickUp)
    }
  }
}
