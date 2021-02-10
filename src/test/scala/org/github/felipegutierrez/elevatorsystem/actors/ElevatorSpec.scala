package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.github.felipegutierrez.elevatorsystem.actors.protocol.{BuildingCoordinatorProtocol, ElevatorProtocol}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ElevatorSpec extends TestKit(ActorSystem("ElevatorSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "an Elevator actor" should {
    val id = 1
    val elevatorActor = system.actorOf(Elevator.props(id, s"elevator_$id"), s"elevator_$id")
    "retrieve its state when it is stop" in {
      elevatorActor ! ElevatorProtocol.RequestElevatorState(1)
      expectMsg(BuildingCoordinatorProtocol.ElevatorState(1, 0, 0, 0))
    }
    "retrieve the right state after moving" in {
      elevatorActor ! ElevatorProtocol.MoveRequest(1, 10)
      expectMsg(BuildingCoordinatorProtocol.MoveRequestSuccess(1, 10))
      elevatorActor ! ElevatorProtocol.MakeMove(1, 10)
      expectMsg(BuildingCoordinatorProtocol.MakeMoveSuccess(1, 10, 1))
    }
  }

  "the elevator that DOES NOT receive a MoveRequest msg" should {
    "not arrive at the a specific floor" in {
      val id = 1
      val nextStop = 5

      val elevatorActor = TestActorRef[Elevator](Elevator.props(id, s"elevator_$id"))
      assert(elevatorActor.underlyingActor.currentFloor == 0)
      assert(elevatorActor.underlyingActor.targetFloor == 0)

      elevatorActor.receive(ElevatorProtocol.MakeMove(id, nextStop))
      assert(elevatorActor.underlyingActor.currentFloor == 0)
      assert(elevatorActor.underlyingActor.targetFloor == 0)
    }
  }

  "the elevator that DOES receive a MoveRequest msg" should {
    "arrive at the a specific floor" in {
      val id = 1
      val nextStop = 5

      val elevatorActor = TestActorRef[Elevator](Elevator.props(id, s"elevator_$id"))
      assert(elevatorActor.underlyingActor.currentFloor == 0)
      assert(elevatorActor.underlyingActor.targetFloor == 0)

      elevatorActor.receive(ElevatorProtocol.MoveRequest(id, nextStop))
      assert(elevatorActor.underlyingActor.currentFloor == 0)
      assert(elevatorActor.underlyingActor.targetFloor == 0)

      elevatorActor.receive(ElevatorProtocol.MakeMove(id, nextStop))
      assert(elevatorActor.underlyingActor.currentFloor == nextStop)
      assert(elevatorActor.underlyingActor.targetFloor == nextStop)
    }
  }
}
