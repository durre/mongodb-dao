name := """mongodb-dao"""
organization := "com.github.durre"
version := "1.2.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "reactivemongo" % "0.12.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "ch.qos.logback" %  "logback-classic" % "1.1.7" % "provided"
)

resolvers += "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/"

publishMavenStyle := true

publishArtifact in (Test, packageBin) := true
publishArtifact in (Test, packageDoc) := true
publishArtifact in (Test, packageSrc) := true
