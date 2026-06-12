import Dependencies._

organization in ThisBuild          := "net.liftweb"
version in ThisBuild               := "3.5.0-lift-persistence"
homepage in ThisBuild              := Some(url("http://www.liftweb.net"))
licenses in ThisBuild              += ("Apache License, Version 2.0", url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
startYear in ThisBuild             := Some(2006)
organizationName in ThisBuild      := "WorldWide Conferencing, LLC"

val scala212Version = "2.12.12"
val scala213Version = "2.13.2"

scalaVersion in ThisBuild          := scala212Version
crossScalaVersions in ThisBuild    := Seq(scala212Version, scala213Version)

libraryDependencies in ThisBuild ++= Seq(specs2, specs2Matchers, specs2Mock, scalacheck, scalactic, scalatest)

scalacOptions in ThisBuild ++= Seq("-deprecation")

pomIncludeRepository in ThisBuild := { _ => false }
scmInfo in ThisBuild := Some(ScmInfo(
  url("https://github.com/hongwei1/framework"),
  "scm:git:https://github.com/hongwei1/framework.git"
))

lazy val `lift-persistence` =
  Project("lift-persistence", file("lift-persistence"))
    .settings(
      description := "Lift Persistence — OBP fork single-artifact ORM (mapper + db + proto + util + common)",
      parallelExecution in Test := false,
      libraryDependencies ++= Seq(
        scala_reflect(scalaVersion.value),
        slf4j_api,
        logback,
        scala_xml,
        joda_time,
        joda_convert,
        commons_codec,
        xerces,
        jbcrypt,
        // test
        h2,
        derby
      ),
      initialize in Test := {
        System.setProperty(
          "derby.stream.error.file",
          ((crossTarget in Test).value / "derby.log").absolutePath
        )
      }
    )
