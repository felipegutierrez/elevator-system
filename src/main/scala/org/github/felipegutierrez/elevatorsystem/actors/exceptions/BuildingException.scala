package org.github.felipegutierrez.elevatorsystem.actors.exceptions

case class BuildingException(msg: String = "Something wrong during building construction :(") extends RuntimeException
