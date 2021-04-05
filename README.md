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

```
stuart{
  # wether to use sandbox or not - default true
  dry-run = true
  # stuart api client id
  api-client-id = ""
  # stuart api secret
  api-secret = ""
}
```

## Usage

### General usage

```scala
StuartApi().aCallTo(aRequest) match {
  case Left(l: StuartError) => // eg StuartError(error = OUT_OF_RANGE, message = This location is out of range, data = Map())
  case Right(r: AResponse) => // ...
}
```

With `StuartError` defined as :

```
message StuartError{
    required string error = 1;
    required string message = 2;
    map<string, ArrayOfString> data = 3 [(scalapb.field).value_type = "Seq[String]"];
}
```

### Validate address

```scala
import app.softnetwork.stuart.client.scala._
import message._
import model._

StuartApi().validateAddress("12 rue rivoli, 75001 Paris") match {
  case Left(l: StuartError) => // eg StuartError(error = OUT_OF_RANGE, message = This location is out of range, data = Map())
  case Right(r: AddressValidated) => // AddressValidated
    if(r.success){
      // ... do stuff
    }
    else{
      // ... do other stuff
    }
}
```

### Request a job Pricing

```scala

val pickups = List(
  Pickup.defaultInstance
    .withAddress("12 rue rivoli, 75001 Paris")
    .withContact(
      ContactRequest.defaultInstance
        .withFirstname("Bobby")
        .withLastname("Brown")
        .withPhone("+33610101010")
    )
)
val dropoffs = List(
  DropOff.defaultInstance
    .withPackageType(PackageType.small)
    .withAddress("Les Arches d'Issy, 92130 Issy-Les-Moulineaux")
    .withContact(
      ContactRequest.defaultInstance
        .withFirstname("Dany")
        .withLastname("Dan")
        .withPhone("+33611112222")
    )
)
val request =
  JobRequest.defaultInstance
    .withTransportType(TransportType.bike)
    .withPickups(pickups)
    .withDropoffs(dropoffs)

StuartApi().calculatePricing(request) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: PricingCalculated) => // eg PricingCalculated(amount = 17, currency = EUR)
}
```

### Validate job parameters

```scala
StuartApi().validateJob(request) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: JobValidated) => 
    if(r.valid.getOrElse(false)){
      // ... do stuff
    }
    else{
      // ... do other stuff
    }
}
```

### Request a job ETA

```scala
StuartApi().eta(request) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: JobEta) => // ... eg JobEta(eta = 54)
}
```

### Create a job

```scala
StuartApi().createJob(request) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: Job) => // ... do something with Job
}
```

### Get a job listing

```scala
import JobStatus._

val jobQuery = JobQuery.defaultInstance
  .withStatus(
    Seq(`new`, searching, in_progress)
  )
  .withPage(1)
  .withPerPage(10)
  .withClientReference("client reference")

StuartApi().listJobs(jobQuery) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: Seq[Job]) => // ... do something with Seq[Job]
}
```

### Get a job

```scala
StuartApi().loadJob(idJob) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: Job) => // ... do something with Job
}
```

### Cancel a job

```scala
StuartApi().cancelJob(idJob) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(_) => // ... do something
}
```
