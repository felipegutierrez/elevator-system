package org.github.felipegutierrez.elevatorsystem.actors.exceptions

case class BuildingCoordinatorException(msg: String = "Something wrong during building construction :(") extends RuntimeException(msg)
