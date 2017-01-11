name := """mongodb-dao"""
organization := "com.github.durre"
version := "1.2.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "reactivemongo" % "0.12.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "ch.qos.logback" %  "logback-classic" % "1.1.7" % "provided"
)

resolvers += "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/"

publishArtifact in (Test, packageBin) := true
publishArtifact in (Test, packageDoc) := true
publishArtifact in (Test, packageSrc) := true
