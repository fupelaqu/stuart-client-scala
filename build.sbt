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

version := "0.2-SNAPSHOT"

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
  "Softnetwork snapshots" at "https://softnetwork.jfrog.io/artifactory/snapshots/",
  "Maven Central Server" at "https://repo1.maven.org/maven2",
  "Typesafe Server" at "https://repo.typesafe.com/typesafe/releases"
)

val typesafeConfig = Seq(
  "com.typesafe"      % "config"   % Versions.typesafeConfig,
  "com.github.kxbmap" %% "configs" % Versions.kxbmap excludeAll ExclusionRule(organization="com.typesafe", name="config")
)

val akka = Seq(
  "com.typesafe.akka" %% "akka-actor" % Versions.akka,
  "com.typesafe.akka" %% "akka-protobuf-v3" % Versions.akka,
  "com.typesafe.akka" %% "akka-stream" % Versions.akka,
  "com.typesafe.akka" %% "akka-slf4j" % Versions.akka,
  "com.typesafe.akka" %% "akka-stream-testkit" % Versions.akka % Test,
  "com.typesafe.akka" %% "akka-testkit"  % Versions.akka % Test
)

val akkaHttp = Seq(
  "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp % Test
)

val scalatest = Seq(
  "org.scalatest"  %% "scalatest"  % Versions.scalatest  % Test,
  "org.scalacheck" %% "scalacheck" % Versions.scalacheck % Test
)

publishArtifact in (Test, packageBin) := true

// enable publishing the test API jar
publishArtifact in (Test, packageDoc) := true

// enable publishing the test sources jar
publishArtifact in (Test, packageSrc) := true

libraryDependencies ++=
  Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    "app.softnetwork.protobuf" %% "scalapb-extensions" % "0.1.2.1",
    "app.softnetwork.api" %% "generic-client-api" % "0.1.3",
    "app.softnetwork.api" %% "generic-client-api" % "0.1.3" classifier "tests",
    "org.apache.commons" % "commons-lang3" % "3.12.0"
  ) ++
  akka ++
  akkaHttp ++
  typesafeConfig ++
  scalatest


unmanagedResourceDirectories in Compile += baseDirectory.value / "src/main/protobuf"

lazy val root = project.in(file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings, BuildInfoSettings.settings, pbSettings)
  .enablePlugins(JavaAppPackaging, BuildInfoPlugin)
