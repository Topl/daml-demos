import sbt._

object Dependencies {
  lazy val bramblVersion = "1.10.2"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.11"
  lazy val brambl = "co.topl" %% "brambl" % bramblVersion
  lazy val bramblCommon = "co.topl" %% "common" % bramblVersion
  lazy val catEffects = "org.typelevel" %% "cats-effect" % "3.3.12"
  lazy val toplDaml = "co.topl.daml" % "topl-daml-api" % "1.0.0-SNAPSHOT"
}
