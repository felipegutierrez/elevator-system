[![Build Status](https://travis-ci.com/felipegutierrez/elevator-system.svg?branch=main)](https://travis-ci.com/felipegutierrez/elevator-system)
[![Coverage Status](https://coveralls.io/repos/github/felipegutierrez/elevator-system/badge.svg)](https://coveralls.io/github/felipegutierrez/elevator-system)
[![CodeFactor](https://www.codefactor.io/repository/github/felipegutierrez/elevator-system/badge)](https://www.codefactor.io/repository/github/felipegutierrez/elevator-system)
![Lines of code](https://img.shields.io/tokei/lines/github/felipegutierrez/elevator-system)

# An elevator system using Akka actors

Using this system one can simulate a building with N floors, up to 16 elevators that receive pickUp messages asynchronously, and uses a controller to coordinate the elevator's movements. Our first controller is based on the First-Come-First-Serve logic.

### Basic commands:

 - Compiling: `sbt compile`.
 - Compiling & Running: `sbt run`.
 - Testing: `sbt test`.
 - Generate documentation: `sbt doc`, then open the `target/scala-2.12/api/index.html` file.



