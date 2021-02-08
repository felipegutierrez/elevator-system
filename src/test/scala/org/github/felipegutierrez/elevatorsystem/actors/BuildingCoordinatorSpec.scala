package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.github.felipegutierrez.elevatorsystem.actors.exceptions.BuildingException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol._
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

    "create Elevator as child actors" in {
      // val elevator01 = system.actorSelection("/user/buildingActorSpec/elevator_1")
      buildingActor ! MoveElevator(1)
      buildingActor ! MoveElevator(1)
    }
    "receive pick_up requests" in {
      val floor = 4
      val direction = +1
      buildingActor ! PickUpRequest(floor, direction)

      expectMsg(PickUpRequestSuccess())
      expectNoMessage()
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
        case BuildingException(msg) =>
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
        case BuildingException(msg) =>
          println(s"I got exception $msg")
          assert(true)
        case _: Throwable => fail("we should get an exception here")
      }
    }
  }
}
