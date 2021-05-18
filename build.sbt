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

version := "0.3.0"

scalaVersion := "2.12.11"

scalacOptions ++= Seq("-deprecation", "-feature")

parallelExecution in Test := false

val pbSettings = Seq(
  PB.targets in Compile := Seq(
    scalapb.gen() -> crossTarget.value / "protobuf_managed/main"
  )
)

resolvers ++= Seq(
  "Softnetwork releases" at "https://softnetwork.jfrog.io/artifactory/releases/",
  "Maven Central Server" at "https://repo1.maven.org/maven2",
  "Typesafe Server" at "https://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++=
  Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "app.softnetwork.protobuf" %% "scalapb-extensions" % "0.1.3",
    "app.softnetwork.api" %% "generic-client-api" % "0.2.0",
    "app.softnetwork.api" %% "generic-server-api" % "0.1.1" excludeAll ExclusionRule(organization = "com.github.dnvriend"),
    "com.github.dnvriend" %% "akka-persistence-inmemory" % "2.5.15.2" excludeAll ExclusionRule(organization = "com.typesafe.akka"),
    "app.softnetwork.api" %% "generic-server-api-testkit" % "0.1.1" % Test,
    "org.apache.commons" % "commons-lang3" % "3.12.0"
  )

unmanagedResourceDirectories in Compile += baseDirectory.value / "src/main/protobuf"

lazy val root = project.in(file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings, BuildInfoSettings.settings, pbSettings)
  .enablePlugins(JavaAppPackaging, BuildInfoPlugin)
