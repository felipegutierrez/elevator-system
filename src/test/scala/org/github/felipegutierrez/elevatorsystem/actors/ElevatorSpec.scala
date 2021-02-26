package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.Direction
import org.github.felipegutierrez.elevatorsystem.actors.protocol.{BuildingCoordinatorProtocol, ElevatorPanelProtocol, ElevatorProtocol}
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
      expectMsg(BuildingCoordinatorProtocol.ElevatorState(1, 0, 0, Direction(0)))
    }
    "retrieve the right state after moving" in {
      elevatorActor ! ElevatorProtocol.MoveRequest(1, 10)
      expectMsg(BuildingCoordinatorProtocol.MoveRequestSuccess(1, 10))
      elevatorActor ! ElevatorProtocol.MakeMove(1, 10)
      expectMsg(BuildingCoordinatorProtocol.MakeMoveSuccess(1, 10, Direction(+1)))
    }
  }

  "the Elevator actor" should {
    "only receive messages that belong to its protocol" in {
      val id = 2
      EventFilter.warning(pattern = s"[Elevator $id] unknown message: [a-z]", occurrences = 0) intercept {
        val elevatorActor = system.actorOf(Elevator.props(id, s"elevator_$id"), s"elevator_$id")
        elevatorActor ! ElevatorProtocol.RequestElevatorState(id)
      }
    }

    "not receive messages that do not belong to its protocol" in {
      val id = 3
      EventFilter.warning(message = s"[Elevator $id] unknown message: PickUp", occurrences = 1) intercept {
        val elevatorActor = system.actorOf(Elevator.props(id, s"elevator_$id"), s"elevator_$id")
        elevatorActor ! ElevatorPanelProtocol.PickUp
      }
    }
  }
}
