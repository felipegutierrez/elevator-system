name := "elevator-system"

version := "0.1"

scalaVersion := "2.13.4"

val scalaBinVersion = "2.13"
val scalaTestVersion = "3.2.0"
val akkaVersion = "2.6.12"

libraryDependencies ++= Seq(
  // Akka basics
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,

  // Scala test
  "org.scalatest" %% "scalatest" % scalaTestVersion,
)

