import Dependencies._


lazy val publishSettings = List(
  publish / skip := true,
  organization := "co.topl",
  homepage := Some(url("https://github.com/Topl/daml-demos/tree/main/scala-daml-broker-app")),
  licenses := List("MPL2.0" -> url("https://www.mozilla.org/en-US/MPL/2.0/")),
  sonatypeCredentialHost := "s01.oss.sonatype.org",
  sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
  developers := List(
    Developer(
      "mundacho",
      "Edmundo Lopez Bobeda",
      "e.lopez@topl.me",
      url("https://github.com/mundacho")
    ),
    Developer(
      "scasplte2",
      "James Aman",
      "j.aman@topl.me",
      url("https://github.com/scasplte2")
    )
    )
)


lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(publishSettings)
  .settings(
    name := "bifrost-daml-broker",
    scalaVersion := "2.13.8",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += brambl,
    libraryDependencies += bramblCommon,
    libraryDependencies += toplDaml,
    libraryDependencies += slf4j
  )
