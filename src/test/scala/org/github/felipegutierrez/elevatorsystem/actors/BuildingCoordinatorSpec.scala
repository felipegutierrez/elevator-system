package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import org.github.felipegutierrez.elevatorsystem.actors.exceptions.BuildingCoordinatorException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.ElevatorPanelProtocol.PickUpRequestSuccess
import org.github.felipegutierrez.elevatorsystem.actors.protocol._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class BuildingCoordinatorSpec
  extends TestKit(ActorSystem("BuildingCoordinatorSpec"))
    with ImplicitSender
    with AnyWordSpecLike
    // with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "the Building actor" should {
    val numberOfFloors: Int = 10
    val numberOfElevators: Int = 1
    val actorName = "buildingActorSpec"
    val buildingActor = system.actorOf(BuildingCoordinator.props(actorName, numberOfFloors, numberOfElevators), actorName)

    "be the only one that moves elevators" in {
      buildingActor ! BuildingCoordinatorProtocol.MoveElevator(1, 1)
      expectNoMessage()
    }
    "receive pick_up requests" in {
      val floor = 4
      val direction = +1
      buildingActor ! BuildingCoordinatorProtocol.PickUpRequest(floor, direction)

      expectMsg(ElevatorPanelProtocol.PickUpRequestSuccess)
      expectNoMessage()
    }
    "move only existing elevators" in {
      buildingActor ! BuildingCoordinatorProtocol.MoveElevator(2, 1)
    }
  }

  "the Building actor with more than 16 elevators" should {
    "not be allowed" in {
      assertThrows[BuildingCoordinatorException] {
        val numberOfFloors: Int = 10
        val numberOfElevators: Int = 17
        val actorName = "buildingActorSpec2"
        system.actorOf(BuildingCoordinator.props(actorName, numberOfFloors, numberOfElevators), actorName)
      }
    }
  }

  "the Building must not be a house, so it" should {
    "have 2 or more floors" in {
      assertThrows[BuildingCoordinatorException] {
        val numberOfFloors: Int = 1
        val numberOfElevators: Int = 17
        val actorName = "buildingActorSpec3"
        system.actorOf(BuildingCoordinator.props(actorName, numberOfFloors, numberOfElevators), actorName)
      }
    }
  }

  "When the Building actor receives a wrong pick up message" should {
    "not be allowed it" in {
      EventFilter[BuildingCoordinatorException](occurrences = 1) intercept {
        val numberOfFloors: Int = 10
        val numberOfElevators: Int = 3
        val actorName = "buildingActorSpec4"

        val buildingActor = system.actorOf(BuildingCoordinator.props(actorName, numberOfFloors, numberOfElevators), actorName)
        val floor = -1
        val direction = +1
        buildingActor ! BuildingCoordinatorProtocol.PickUpRequest(floor, direction)
      }
    }
  }

  "when a PickUpRequest comes from the ground floor to go DOWN (-1) it" should {
    "NOT allow" in {
      EventFilter[BuildingCoordinatorException](occurrences = 1) intercept {
        val numberOfFloors: Int = 10
        val numberOfElevators: Int = 1
        val actorName = "buildingActorSpec5"

        val buildingActor = system.actorOf(BuildingCoordinator.props(actorName, numberOfFloors, numberOfElevators), actorName)
        val floor = 0
        val direction = -1
        buildingActor ! BuildingCoordinatorProtocol.PickUpRequest(floor, direction)
        expectNoMessage()
      }
    }
  }

  "when a PickUpRequest comes from the last floor to go UP (+1) it" should {
    "NOT allow" in {
      EventFilter[BuildingCoordinatorException](occurrences = 1) intercept {
        val numberOfFloors: Int = 10
        val numberOfElevators: Int = 1
        val actorName = "buildingActorSpec6"

        val buildingActor = system.actorOf(BuildingCoordinator.props(actorName, numberOfFloors, numberOfElevators), actorName)
        val floor = 10
        val direction = 1
        buildingActor ! BuildingCoordinatorProtocol.PickUpRequest(floor, direction)
        expectNoMessage()
      }
    }
  }

  "the BuildingCoordinator actor" should {
    "only receive messages that belong to its protocol" in {
      EventFilter.warning(pattern = "[BuildingCoordinator] unknown message: [a-z]", occurrences = 0) intercept {
        val buildingActor = system.actorOf(BuildingCoordinator.props("building", 10, 2), "building")
        buildingActor ! BuildingCoordinatorProtocol.PickUpRequest(+4, +1)
        buildingActor ! BuildingCoordinatorProtocol.MoveElevator(1, +1)
        buildingActor ! BuildingCoordinatorProtocol.DropOffRequest(1, 8, +1)
      }
    }

    "not receive messages that do not belong to its protocol" in {
      val msg = PickUpRequestSuccess
      EventFilter.warning(message = s"[BuildingCoordinator] unknown message: $msg", occurrences = 1) intercept {
        val buildingActor = system.actorOf(BuildingCoordinator.props("building1", 10, 2), "building1")
        buildingActor ! msg
      }
    }
  }
}
