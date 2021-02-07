package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.github.felipegutierrez.elevatorsystem.actors.exceptions.BuildingException
import org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class BuildingSpec extends TestKit(ActorSystem("BuildingSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "the Building actor" should {
    val numberOfFloors: Int = 10
    val numberOfElevators: Int = 2
    val buildingActor = system.actorOf(Building.props(numberOfFloors, numberOfElevators), "buildingActorSpec")

    "create Elevator as child actors" in {
      val elevator01 = system.actorSelection("/user/buildingActorSpec/elevator_1")
      val elevator02 = system.actorSelection("/user/buildingActorSpec/elevator_2")
      val elevator03 = system.actorSelection("/user/buildingActorSpec/elevator_3")

      elevator01 ! MoveRequest()
      elevator02 ! MoveRequest()
      elevator03 ! MoveRequest()

      expectMsg(MoveRequestSuccess())
      expectMsg(MoveRequestSuccess())
      expectNoMessage()
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
      try {
        val buildingActor = system.actorOf(Building.props(numberOfFloors, numberOfElevators), "buildingActorSpec2")
      } catch {
        case BuildingException(msg) =>
          println(s"I got exception $msg")
          assert(true)
        case _ => fail("we should get an exception here")
      }
    }
  }
  "the Building must not be a house, so it" should {
    "have 2 or more floors" in {
      val numberOfFloors: Int = 1
      val numberOfElevators: Int = 17
      try {
        val buildingActor = system.actorOf(Building.props(numberOfFloors, numberOfElevators), "buildingActorSpec3")
      } catch {
        case BuildingException(msg) =>
          println(s"I got exception $msg")
          assert(true)
        case _ => fail("we should get an exception here")
      }
    }
  }
}
