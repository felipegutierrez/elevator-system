package org.github.felipegutierrez.elevatorsystem.states

/**
 * The state of the Elevator which holds this ElevatorState attribute.
 *
 * @param id           the ID of the Elevator that holds this state
 * @param currentFloor the current floor of the elevator with this state ElevatorState
 * @param targetFloor  the target floor of the elevator with this state ElevatorState
 * @param nextStops    the next stops of the Elevator with this state ElevatorState
 */
case class ElevatorState(id: Int,
                         currentFloor: Int,
                         targetFloor: Int,
                         nextStops: Array[Int]) extends State(id) {


}
