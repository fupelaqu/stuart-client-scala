logLevel := Level.Warn

resolvers ++= Seq(
  "Softnetwork releases" at "https://softnetwork.jfrog.io/artifactory/releases/",
  "Typesafe Server" at "https://repo.typesafe.com/typesafe/releases"
)

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.10")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.9.3")

addSbtPlugin("app.softnetwork.sbt-softnetwork" % "sbt-softnetwork-git" % "0.1.6")

addSbtPlugin("app.softnetwork.sbt-softnetwork" % "sbt-softnetwork-publish" % "0.1.6")

addSbtPlugin("app.softnetwork.sbt-softnetwork" % "sbt-softnetwork-info" % "0.1.6")

addSbtPlugin("app.softnetwork.sbt-softnetwork" % "sbt-softnetwork-protoc" % "0.1.6")
