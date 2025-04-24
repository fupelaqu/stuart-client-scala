import app.softnetwork.*

/////////////////////////////////
// Defaults
/////////////////////////////////

organization := "app.softnetwork.stuart"

name := "stuart-client-scala"

version := "0.6.0"

scalaVersion := "2.12.18"

scalacOptions ++= Seq("-deprecation", "-feature", "-target:jvm-1.8", "-Ypartial-unification")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

Test / parallelExecution := false

resolvers ++= Seq(
  "Softnetwork releases" at "https://softnetwork.jfrog.io/artifactory/releases/",
  "Softnetwork snapshots" at "https://softnetwork.jfrog.io/artifactory/snapshots/",
  "Maven Central Server" at "https://repo1.maven.org/maven2",
  "Typesafe Server" at "https://repo.typesafe.com/typesafe/releases"
)

libraryDependencies ++=
  Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1",
    "app.softnetwork.protobuf" %% "scalapb-extensions" % "0.1.8",
    "app.softnetwork.api" %% "generic-client-api" % Versions.clientApi,
    "app.softnetwork.api" %% "generic-server-api" % Versions.genericPersistence,
    "app.softnetwork.api" %% "generic-server-api-testkit" % Versions.genericPersistence % Test,
    "org.apache.commons" % "commons-lang3" % "3.12.0"
  )

ThisBuild / libraryDependencySchemes ++= Seq(
  "io.circe" %% "circe-core" % VersionScheme.Always,
  "io.circe" %% "circe-generic" % VersionScheme.Always,
  "io.circe" %% "circe-parser" % VersionScheme.Always
)

lazy val root = project.in(file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings, Protoc.protocSettings, app.softnetwork.Info.infoSettings)
  .enablePlugins(JavaAppPackaging, BuildInfoPlugin)
