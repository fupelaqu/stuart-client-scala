import app.softnetwork.*

/////////////////////////////////
// Defaults
/////////////////////////////////

organization := "app.softnetwork.stuart"

name := "stuart-client-scala"

version := "0.5.2"

scalaVersion := "2.12.18"

scalacOptions ++= Seq("-deprecation", "-feature")

Test / parallelExecution := false

resolvers ++= Seq(
  "Softnetwork releases" at "https://softnetwork.jfrog.io/artifactory/releases/",
  "Maven Central Server" at "https://repo1.maven.org/maven2",
  "Typesafe Server" at "https://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++=
  Seq(
    "app.softnetwork.protobuf" %% "scalapb-extensions" % "0.1.8",
    "app.softnetwork.api" %% "generic-client-api" % "0.2.4",
    "app.softnetwork.api" %% "generic-server-api" % "0.3.5",
    "app.softnetwork.api" %% "generic-server-api-testkit" % "0.3.5" % Test,
    "org.apache.commons" % "commons-lang3" % "3.12.0"
  )

ThisBuild / libraryDependencySchemes ++= Seq(
  "io.circe" %% "circe-core" % VersionScheme.Always,
  "io.circe" %% "circe-generic" % VersionScheme.Always,
  "io.circe" %% "circe-parser" % VersionScheme.Always
)

lazy val root = project.in(file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings, Protoc.protocSettings)
  .enablePlugins(JavaAppPackaging, BuildInfoPlugin)
