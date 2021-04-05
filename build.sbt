import sbt.Resolver

import Common._
import app.softnetwork.sbt.build._

/////////////////////////////////
// Defaults
/////////////////////////////////

app.softnetwork.sbt.build.Publication.settings

/////////////////////////////////
// Useful aliases
/////////////////////////////////

addCommandAlias("cd", "project") // navigate the projects

addCommandAlias("cc", ";clean;compile") // clean and compile

addCommandAlias("pl", ";clean;publishLocal") // clean and publish locally

addCommandAlias("pr", ";clean;publish") // clean and publish globally

shellPrompt := prompt

organization := "app.softnetwork.stuart"

name := "stuart-client-scala"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.11"

scalacOptions ++= Seq("-deprecation", "-feature")

parallelExecution in Test := false

val pbSettings = Seq(
  PB.targets in Compile := Seq(
    scalapb.gen() -> crossTarget.value / "protobuf_managed/main"
  )
)

resolvers ++= Seq(
  "Artifactory" at "https://softnetwork.jfrog.io/artifactory/releases/",
  "Maven Central Server" at "https://repo1.maven.org/maven2",
  "Typesafe Server" at "https://repo.typesafe.com/typesafe/releases"
)

val typesafeConfig = Seq(
  "com.typesafe"      % "config"   % Versions.typesafeConfig,
  "com.github.kxbmap" %% "configs" % Versions.kxbmap excludeAll ExclusionRule(organization="com.typesafe", name="config")
)

libraryDependencies ++=
  Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "app.softnetwork.protobuf" %% "scalapb-extensions" % "0.1.1",
    "app.softnetwork.api" %% "generic-client-api" % "0.1.2",
    "org.apache.commons" % "commons-lang3" % "3.12.0"
  ) ++
  typesafeConfig

lazy val root = project.in(file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings, BuildInfoSettings.settings, pbSettings)
  .enablePlugins(JavaAppPackaging, BuildInfoPlugin)
