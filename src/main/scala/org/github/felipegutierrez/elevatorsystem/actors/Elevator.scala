package org.github.felipegutierrez.elevatorsystem.actors

import org.github.felipegutierrez.elevatorsystem.states.ElevatorState

/**
 * The Elevator of the Building.
 * This is an actor that receives/sends messages from/to the Building
 *
 * @param state the state of this Elevator
 */
case class Elevator(state: ElevatorState) {

}
