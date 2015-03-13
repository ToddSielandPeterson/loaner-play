import play.PlayScala
import sbt._
import Keys._

name := "loaner"

version := "0.02"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

val jodaTime      = "joda-time" % "joda-time"           % "2.5" % "compile->default" intransitive ()
val jodaMoney     = "org.joda"  % "joda-money"          % "0.9.1" % "compile->default" intransitive ()
val jodaConvert   = "org.joda"  % "joda-convert"        % "1.7" % "compile->default" intransitive ()

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  jodaTime,
  jodaMoney,
  jodaConvert,
  "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
  "com.thoughtworks.xstream" % "xstream" % "1.4.7",
  "org.mongodb" % "mongo-java-driver" % "2.13.0-rc1",
  "org.mongodb" %% "casbah-commons" % "2.8.0-RC0",
  "org.mongodb" %% "casbah" % "2.8.0-RC0",
  "commons-io" % "commons-io" % "2.4"
)
