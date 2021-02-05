package org.github.felipegutierrez.elevatorsystem.actors

import akka.actor.{Actor, ActorLogging, Props}
import org.github.felipegutierrez.elevatorsystem.controllers.ElevatorControlSystem
import org.github.felipegutierrez.elevatorsystem.states.BuildingState

object Building {
  def props(state: BuildingState,
            elevators: Seq[Elevator],
            elevatorControlSystem: ElevatorControlSystem) = Props(new Building(state, elevators, elevatorControlSystem))
}

/**
 * This is the building actor that has the elevators and its control system
 *
 * @param state                 the state of this Building.
 * @param elevators             the list of Elevators that this Building has
 * @param elevatorControlSystem the control system responsible to schedule the movement of elevators in this Building
 */
case class Building(state: BuildingState,
                    elevators: Seq[Elevator],
                    elevatorControlSystem: ElevatorControlSystem) extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg => log.warning(s"unknown message: $msg")
  }
}


