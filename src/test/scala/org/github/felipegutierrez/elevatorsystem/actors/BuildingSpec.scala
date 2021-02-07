package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.github.felipegutierrez.elevatorsystem.actors.protocol.Protocol.{MoveRequest, MoveRequestSuccess}
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
    "create Elevator as child actors" in {
      val numberOfFloors: Int = 10
      val numberOfElevators: Int = 2
      val buildingActor = system.actorOf(Building.props(numberOfFloors, numberOfElevators), "buildingActorSpec")

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
  }
}
