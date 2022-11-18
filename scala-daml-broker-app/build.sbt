import Dependencies._

resolvers += Resolver.mavenLocal

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "topl-daml-broker",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += brambl,
    libraryDependencies += bramblCommon,
    libraryDependencies += toplDaml
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
