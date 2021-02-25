[![Build Status](https://travis-ci.com/felipegutierrez/elevator-system.svg?branch=main)](https://travis-ci.com/felipegutierrez/elevator-system)
[![Coverage Status](https://coveralls.io/repos/github/felipegutierrez/elevator-system/badge.svg)](https://coveralls.io/github/felipegutierrez/elevator-system)
[![CodeFactor](https://www.codefactor.io/repository/github/felipegutierrez/elevator-system/badge)](https://www.codefactor.io/repository/github/felipegutierrez/elevator-system)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/dd54cc02e6ee4b0e8bd3c67443934bef)](https://www.codacy.com/gh/felipegutierrez/elevator-system/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=felipegutierrez/elevator-system&amp;utm_campaign=Badge_Coverage)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/09bd5e81d9ff443ca238bc079e22f6fc)](https://app.codacy.com/gh/felipegutierrez/elevator-system?utm_source=github.com&utm_medium=referral&utm_content=felipegutierrez/elevator-system&utm_campaign=Badge_Grade_Settings)
![Lines of code](https://img.shields.io/tokei/lines/github/felipegutierrez/elevator-system)

# An elevator system using Akka actors

The system is composed of three actors named Panel, BuildingCoordinator, and Elevators. 
The BuildingCoordinator is the dispatcher that coordinates how the elevators move on the building. 
To accomplish this task uses one ElevatorControlSystem which can be a controller that implements the FCFS algorithm at ElevatorControlSystemFCFS or a controller that implements the SCAN algorithm at ElevatorControlSystemScan. 
The communication among the actors are done using messages on the ElevatorPanelProtocol, the BuildingCoordinatorProtocol, and the ElevatorProtocol.

The Panel is responsible to send commands (PickUp(floor, direction)) to the BuildingCoordinator that receives it as PickUpRequest. 
It changes its state that is composed by two maps of pickups and stops, then selects one of its Elevators to move. 
The BuildingCoordinator sends a MoveElevator message which makes the specific Elevator to move. Elevators have only two states. 
They can be stopped or moving. 
If one Elevator receives messages to move when it is already moving, the message is stashed and will be processed once the elevator stops. 
It is the BuildingCoordinator that decides which order of movements it will send to the Elevators. 
This is dictated by the FCFS or the SCAN controller.

### Basic commands:

 - Compiling: `sbt compile`.
 - Compiling & Running: `sbt run`.
 - Unit tests: `sbt test`.
 - Generate documentation: `sbt doc`, then open the `target/scala-2.12/api/index.html` file.



