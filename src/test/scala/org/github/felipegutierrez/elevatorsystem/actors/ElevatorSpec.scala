package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol._
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
      elevatorActor ! RequestElevatorState(1)
      expectMsg(ElevatorState(1, 0, 0, 0))
    }
    "retrieve the right state after moving" in {
      elevatorActor ! MoveRequest(1, 10)
      elevatorActor ! RequestElevatorState(1)
      expectMsg(ElevatorState(1, 10, 10, 0))
    }
  }
}
