package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, ImplicitSender, TestActorRef, TestKit}
import org.github.felipegutierrez.elevatorsystem.actors.exceptions.BuildingCoordinatorException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.{BuildingCoordinatorProtocol, ElevatorPanelProtocol}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class BuildingCoordinatorSpec extends TestKit(ActorSystem("BuildingCoordinatorSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
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
      buildingActor ! BuildingCoordinatorProtocol.MoveElevator(1)
      expectNoMessage()
    }
    "receive pick_up requests" in {
      val floor = 4
      val direction = +1
      buildingActor ! BuildingCoordinatorProtocol.PickUpRequest(floor, direction)

      expectMsg(ElevatorPanelProtocol.PickUpRequestSuccess())
      expectNoMessage()
    }
    "move only existing elevators" in {
      buildingActor ! BuildingCoordinatorProtocol.MoveElevator(2)
    }
  }

  "the Building actor that handle pick_up requests" should {
    val numberOfFloors: Int = 10
    val numberOfElevators: Int = 1
    val actorName = "buildingStatefulActorSpec"
    "change the pickUpRequests and stopsRequests state" in {
      val buildingActor = TestActorRef[BuildingCoordinator](BuildingCoordinator.props(actorName, numberOfFloors, numberOfElevators))

      val floor01 = 4
      val floor02 = 5
      val floor03 = 10
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor01, +1))
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor02, +1))
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor03, -1))

      assert(buildingActor.underlyingActor.pickUpRequests.get(1).get.contains(floor01))
      assert(buildingActor.underlyingActor.pickUpRequests.get(1).get.contains(floor02))
      assert(buildingActor.underlyingActor.pickUpRequests.get(1).get.contains(floor03))

      assert(buildingActor.underlyingActor.stopsRequests.get(1).get.contains(floor01))
      assert(buildingActor.underlyingActor.stopsRequests.get(1).get.contains(floor02))
      assert(buildingActor.underlyingActor.stopsRequests.get(1).get.contains(floor03))
    }
  }

  "the Building actor that handle drop_off requests" should {
    val numberOfFloors: Int = 10
    val numberOfElevators: Int = 1
    val actorName = "buildingStatefulActorSpec"
    "change only stopsRequests state" in {
      val buildingActor = TestActorRef[BuildingCoordinator](BuildingCoordinator.props(actorName, numberOfFloors, numberOfElevators))

      val floor01 = 4
      val floor02 = 5
      val floor03 = 10
      buildingActor.receive(BuildingCoordinatorProtocol.DropOffRequest(floor01, +1))
      buildingActor.receive(BuildingCoordinatorProtocol.DropOffRequest(floor02, +1))
      buildingActor.receive(BuildingCoordinatorProtocol.DropOffRequest(floor03, -1))

      assert(buildingActor.underlyingActor.pickUpRequests.size == 0)
      assert(buildingActor.underlyingActor.stopsRequests.size == 3)
    }
  }

  "the Building actor with ONE elevator can manage pick_up requests" should {
    val numberOfFloors: Int = 10
    val numberOfElevators: Int = 1
    val actorName = "buildingStatefulActorSpec"
    "remove specific pickups and stops requests" in {
      val buildingActor = TestActorRef[BuildingCoordinator](BuildingCoordinator.props(actorName, numberOfFloors, numberOfElevators))

      val floor01 = 4
      val floor02 = 5
      val floor03 = 10
      val floor04 = 6
      val floor05 = 8
      val floor06 = 2
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor01, +1))
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor02, +1))
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor03, -1))
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor04, +1))
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor05, -1))
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor06, +1))

      buildingActor.underlyingActor.removeStopRequest(1, floor03)
      buildingActor.underlyingActor.removePickUpRequest(1, floor03)

      assert(buildingActor.underlyingActor.pickUpRequests.get(1).get.contains(floor01))
      assert(buildingActor.underlyingActor.pickUpRequests.get(1).get.contains(floor02))
      assertResult(false)(buildingActor.underlyingActor.pickUpRequests.get(1).get.contains(floor03))
      assert(buildingActor.underlyingActor.pickUpRequests.get(1).get.contains(floor04))
      assert(buildingActor.underlyingActor.pickUpRequests.get(1).get.contains(floor05))
      assert(buildingActor.underlyingActor.pickUpRequests.get(1).get.contains(floor06))

      assert(buildingActor.underlyingActor.stopsRequests.get(1).get.contains(floor01))
      assert(buildingActor.underlyingActor.stopsRequests.get(1).get.contains(floor02))
      assertResult(false)(buildingActor.underlyingActor.stopsRequests.get(1).get.contains(floor03))
      assert(buildingActor.underlyingActor.stopsRequests.get(1).get.contains(floor04))
      assert(buildingActor.underlyingActor.stopsRequests.get(1).get.contains(floor05))
      assert(buildingActor.underlyingActor.stopsRequests.get(1).get.contains(floor06))
    }
  }

  "the Building actor with TWO elevator can manage pick_up requests" should {
    val numberOfFloors: Int = 10
    val numberOfElevators: Int = 2
    val actorName = "buildingStatefulActorSpec"
    "remove specific pickups and stops requests" in {
      val buildingActor = TestActorRef[BuildingCoordinator](BuildingCoordinator.props(actorName, numberOfFloors, numberOfElevators))

      val floor01 = 4
      val floor02 = 5
      val floor03 = 10
      val floor04 = 6
      val floor05 = 8
      val floor06 = 2
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor01, +1))
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor02, +1))
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor03, -1))
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor04, +1))
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor05, -1))
      buildingActor.receive(BuildingCoordinatorProtocol.PickUpRequest(floor06, +1))

      buildingActor.underlyingActor.removeStopRequest(1, floor03)
      buildingActor.underlyingActor.removePickUpRequest(1, floor03)
      buildingActor.underlyingActor.removeStopRequest(2, floor02)
      buildingActor.underlyingActor.removePickUpRequest(2, floor02)

      assert(buildingActor.underlyingActor.pickUpRequests.get(1).get.contains(floor01))
      assertResult(false)(buildingActor.underlyingActor.pickUpRequests.get(2).get.contains(floor02))
      assertResult(false)(buildingActor.underlyingActor.pickUpRequests.get(1).get.contains(floor03))
      assert(buildingActor.underlyingActor.pickUpRequests.get(2).get.contains(floor04))
      assert(buildingActor.underlyingActor.pickUpRequests.get(1).get.contains(floor05))
      assert(buildingActor.underlyingActor.pickUpRequests.get(2).get.contains(floor06))

      assert(buildingActor.underlyingActor.stopsRequests.get(1).get.contains(floor01))
      assertResult(false)(buildingActor.underlyingActor.stopsRequests.get(2).get.contains(floor02))
      assertResult(false)(buildingActor.underlyingActor.stopsRequests.get(1).get.contains(floor03))
      assert(buildingActor.underlyingActor.stopsRequests.get(2).get.contains(floor04))
      assert(buildingActor.underlyingActor.stopsRequests.get(1).get.contains(floor05))
      assert(buildingActor.underlyingActor.stopsRequests.get(2).get.contains(floor06))
    }
  }

  "the Building actor with more than 16 elevators" should {
    "not be allowed" in {
      val numberOfFloors: Int = 10
      val numberOfElevators: Int = 17
      val actorName = "buildingActorSpec2"
      try {
        val buildingActor = system.actorOf(BuildingCoordinator.props(actorName, numberOfFloors, numberOfElevators), actorName)
      } catch {
        case BuildingCoordinatorException(msg) =>
          println(s"I got exception $msg")
          assert(true)
        case _: Throwable => fail("we should get an exception here")
      }
    }
  }

  "the Building must not be a house, so it" should {
    "have 2 or more floors" in {
      val numberOfFloors: Int = 1
      val numberOfElevators: Int = 17
      try {
        val actorName = "buildingActorSpec3"
        val buildingActor = system.actorOf(BuildingCoordinator.props(actorName, numberOfFloors, numberOfElevators), actorName)
      } catch {
        case BuildingCoordinatorException(msg) =>
          println(s"I got exception $msg")
          assert(true)
        case _: Throwable => fail("we should get an exception here")
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
}
