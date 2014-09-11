import sbt.Keys._
import sbt._

object AuctionScalaProject extends Build with BuildExtra{
  import Resolvers._
  lazy val root = Project("auction-system", file(".")) settings(coreSettings : _*)

  lazy val commonSettings: Seq[Setting[_]] = Seq(
    organization := "auction-system",
    version := "0.2",
    scalaVersion := "2.10.4",
    crossScalaVersions := Seq("2.10.4", "2.11.1"),
    scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:postfixOps"),
    resolvers ++= Seq(akkaRelease, akkaSnapshot, sonatypeRelease, sonatypeSnapshot)
  )

  lazy val coreSettings = commonSettings ++ Seq(
    name := "auction-system",
    libraryDependencies :=
      Seq(
        "ch.qos.logback"      % "logback-classic"  % "1.0.13",
        "com.typesafe.akka"  %% "akka-actor"       % "2.3.0",
        "com.typesafe.akka"  %% "akka-slf4j"       % "2.3.0",
        "com.typesafe"         %   "config"            % "1.0.0",
        "com.typesafe"        %% "scalalogging-slf4j" % "1.0.1",
        "com.googlecode.concurrentlinkedhashmap"  %   "concurrentlinkedhashmap-lru" % "1.3.2",
        "org.scalautils" % "scalautils_2.10" % "2.0",
        "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
      ),

    parallelExecution in Test := false,

    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },

    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { repo => false }

  )
}


object Resolvers {
  val akkaRelease = "typesafe release repo" at "http://repo.typesafe.com/typesafe/releases/"
  val akkaSnapshot = "typesafe snapshot repo" at "http://repo.typesafe.com/typesafe/snapshots/"
  val sonatypeSnapshot = "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
  val sonatypeRelease = "Sonatype releases"  at "https://oss.sonatype.org/content/repositories/releases/"


}
