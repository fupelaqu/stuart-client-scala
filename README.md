# stuart-client-scala

A client for Stuart API written in scala which relies on [generic-api-client](https://github.com/fupelaqu/generic-client-api) and [Protocol buffers](https://developers.google.com/protocol-buffers/) using [ScalaPB compiler](https://scalapb.github.io/)

For a complete documentation of all endpoints offered by the Stuart API, you can visit [Stuart API documentation](https://stuart.api-docs.io).

## Installation

```scala
resolvers ++= Seq(
  "Artifactory" at "https://softnetwork.jfrog.io/artifactory/snapshots/",
  "Artifactory" at "https://softnetwork.jfrog.io/artifactory/releases/"
)

libraryDependencies += "app.softnetwork.stuart" %% "stuart-client-scala" % "0.1-SNAPSHOT"
```

## Configuration

```stuart{
  # wether to use sandbox or not - default true
  dry-run = true
  # stuart api client id
  api-client-id = ""
  # stuart api secret
  api-secret = ""
}
```

## Usage
