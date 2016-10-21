name := """mongodb-dao"""
organization := "se.durre"
version := "1.0.0"

scalaVersion := "2.11.8"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "org.reactivemongo" %% "reactivemongo" % "0.12.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "ch.qos.logback" %  "logback-classic" % "1.1.7" % "provided"
)

resolvers += "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/"

publishArtifact in (Test, packageBin) := true
publishArtifact in (Test, packageDoc) := true
publishArtifact in (Test, packageSrc) := true