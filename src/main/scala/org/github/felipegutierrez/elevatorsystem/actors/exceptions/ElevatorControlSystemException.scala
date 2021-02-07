package org.github.felipegutierrez.elevatorsystem.actors.exceptions

case class ElevatorControlSystemException(msg: String = "Something wrong on the elevator control system :(") extends RuntimeException
