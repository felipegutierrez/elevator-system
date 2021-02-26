package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import org.github.felipegutierrez.elevatorsystem.actors.protocol.BuildingCoordinatorProtocol.Direction
import org.github.felipegutierrez.elevatorsystem.actors.protocol._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class PanelSpec
  extends TestKit(ActorSystem("PanelSpec"))
    with ImplicitSender
    with AnyWordSpecLike
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "the Panel actor" should {
    "receive messages that belong to its protocol" in {
      EventFilter.warning(pattern = "[Panel] unknown message: [a-z]", occurrences = 0) intercept {
        val panelActor = system.actorOf(Props[Panel], "panelActorSpec")
        val buildingActor = system.actorOf(BuildingCoordinator.props("building", 10, 2), "building")
        panelActor ! ElevatorPanelProtocol.PickUp(4, Direction(+1), buildingActor)
        panelActor ! ElevatorPanelProtocol.PickUpRequestSuccess
        panelActor ! ElevatorPanelProtocol.PickUpRequestFailure
      }
    }

    "not receive messages that do not belong to its protocol" in {
      EventFilter.warning(message = "[Panel] unknown message: ElevatorState", occurrences = 1) intercept {
        val panelActor = system.actorOf(Props[Panel])
        panelActor ! BuildingCoordinatorProtocol.ElevatorState
      }
    }
  }
}
