name := "elevator-system"

version := "0.1"

// scalaVersion := "2.13.4"
scalaVersion := "2.12.7"

val scalaBinVersion = "2.12"
val scalaTestVersion = "3.2.0"
val akkaVersion = "2.6.12"
val logbackVersion = "1.2.3"
val scalaLoggingVersion = "3.9.2"

libraryDependencies ++= Seq(
  // Akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,

  // Scala test
  "org.scalatest" %% "scalatest" % scalaTestVersion,

  // Log
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
)
