name := """Coffee omelette"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "io.spray" %% "spray-json" % "1.3.2",
  "io.spray" %% "spray-routing" % "1.3.3",
  "io.spray" %% "spray-can" % "1.3.3",
  "org.mongodb" %% "casbah" % "2.8.1"
)

Revolver.settings

EclipseKeys.createSrc := EclipseCreateSrc.All
