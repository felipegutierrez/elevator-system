package org.github.felipegutierrez.elevatorsystem.states

/**
 * The state of the Building.
 *
 * @param id             the ID of the Building that holds this state.
 * @param numberOfFloors the number of floors that the Building holding this state has.
 */
case class BuildingState(id: Int,
                         numberOfFloors: Int) extends State(id) {

}
