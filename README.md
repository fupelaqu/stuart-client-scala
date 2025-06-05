![Build Status](https://github.com/fupelaqu/stuart-client-scala/workflows/Build/badge.svg)
[![codecov](https://codecov.io/gh/fupelaqu/stuart-client-scala/branch/main/graph/badge.svg)](https://codecov.io/gh/fupelaqu/stuart-client-scala/)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f7140978c1e0443e8c0bf7765b56b517)](https://www.codacy.com/gh/fupelaqu/stuart-client-scala/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fupelaqu/stuart-client-scala&amp;utm_campaign=Badge_Grade)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# stuart-client-scala

A client for Stuart API written in scala which relies on [generic-client-api](https://github.com/fupelaqu/generic-client-api) and [Protocol buffers](https://developers.google.com/protocol-buffers/) using [ScalaPB compiler](https://scalapb.github.io/)

For a complete documentation of all endpoints and web hooks offered by the Stuart API, you can visit [Stuart API documentation](https://api-docs.stuart.com).

## Installation

```scala
resolvers += "Artifactory" at "https://softnetwork.jfrog.io/artifactory/releases/"

libraryDependencies += "app.softnetwork.stuart" %% "stuart-client-scala" % "0.5.3"
```

## Configuration

```
stuart{
  # stuart client configuration
  client{
    # wether to use sandbox or not - default true
    dry-run = true
    # stuart api client id
    api-client-id = ""
    # stuart api secret
    api-secret = ""
    # stuart tax - default 20
    tax = 20
  }
  # stuart server configuration
  server{
    # uri to handle stuart web hooks - default stuart
    path = "stuart"
    # stuart Webhook authentication
    authentication {
      # stuart Webhook authentication header - default X-STUART-SANDBOX
      header = "X-STUART-SANDBOX"
      # stuart Webhook authentication key - default changeit
      key = "changeit"
    }
  }
}
```

## StuartApi

### General usage

StuartApi returns essentially futures of type `Future[Either[StuartError, aResponse]]`

In order to return directly an `Either[StuartError, aResponse]` you may use the implicit function `sync`

```scala
import app.softnetwork.api.client.ApiCompletion._

StuartApi().aCallTo(aRequest) sync {
  case Left(l: StuartError) => // eg StuartError(error = OUT_OF_RANGE, message = This location is out of range, data = Map())
  case Right(r: aResponse) => // ...
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
import app.softnetwork.stuart.client._
import app.softnetwork.stuart.message._
import app.softnetwork.stuart.model._

StuartApi().validateAddress("12 rue rivoli, 75001 Paris") sync {
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

### List zones per country

```scala
val zones = StuartApi().listZonesPerCountry("france")
```

### Check if a zone exists

```scala
if(StuartApi().checkZone("france", "Charleville-Mézières")){
  // ... do stuff
}
else{
  // ... do other stuff
}
```

### Request a job Pricing

```scala

val client_reference = "client reference"

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
    .withClientReference(client_reference)
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
    // can not schedule a dropoff in less than 60 minutes from now
    .withDropoffAt(ZonedDateTime.now().plusHours(1).plusMinutes(1))
    .withPickups(pickups)
    .withDropoffs(dropoffs)

StuartApi().calculatePricing(request) sync {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: Pricing) => // eg Pricing(currency = EUR, tax_percentage = 0.2, price_tax_included = 21.34, price_tax_excluded = 17.78, tax_amount = 3.56)
}
```

### Validate job parameters

```scala
StuartApi().validateJob(request) sync {
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

### Request a job ETA to pickup

```scala
StuartApi().eta(request) sync {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: JobEta) => // ... eg JobEta(eta = 54)
}
```

### Request a job ETA to dropoff

```scala
StuartApi().cpt(request) sync {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: JobCpt) => // ... eg JobCpt(seconds = 600)
}
```

### Create a job

```scala
var job_id: Int = _

var delivery_id: Int = _

StuartApi().createJob(request) sync {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: Job) => 
    job_id = r.id
    delivery_id = r.deliveries.head.id
    // ... do something with Job
}
```

### Get a job listing

```scala
import JobStatus._

val jobQuery = JobQuery.defaultInstance
  .withStatus(
    Seq(`new`, searching, in_progress, scheduled)
  )
  .withPage(1)
  .withPerPage(10)
  .withClientReference("client reference")

StuartApi().listJobs(jobQuery) sync {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: Seq[Job]) => // ... do something with Seq[Job]
}
```

### Get a job

```scala
StuartApi().getJob(s"$job_id") sync {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: Job) => // ... do something with Job
}
```

### Get driver's anonymous phone number

```scala
StuartApi().getDriverPhoneNumber(s"$delivery_id") sync {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: DriverPhoneNumber) => // ... do something with DriverPhoneNumber
}
```

### Update a job

```scala
val patch = JobPatch.defaultInstance.withDeliveries(
  Seq(DeliveryPatch.defaultInstance
    .withId(delivery_id.toString)
    .withPackageDescription("description")
  )
)

StuartApi().updateJob(s"$job_id", patch) sync {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(_) => // ... do something
}
```

### Cancel a job

```scala
StuartApi().cancelJob(s"$job_id") sync {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(_) => // ... do something
}
```

### Cancel a delivery

```scala
StuartApi().cancelDelivery(s"$delivery_id") sync {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(_) => // ... do something
}
```

## StuartWebHooks

### General usage

```scala
import app.softnetwork.stuart.server.StuartWebHooks
import app.softnetwork.stuart.message.{DeliveryEvent, DriverEvent, JobEvent}

trait MyStuartWebHooks extends StuartWebHooks {
  /**
    *
    * @param job - the created job event
    */
  override def jobCreated(job: JobEvent): Unit = {
    // ... do something with the event
  }

  /**
    *
    * @param job - the updated job event
    */
  override def jobUpdated(job: JobEvent): Unit = {
    // ... do something with the event
  }


  /**
    *
    * @param delivery - the created delivery event
    */
  override def deliveryCreated(delivery: DeliveryEvent): Unit = {
    // ... do something with the event
  }

  /**
    *
    * @param delivery - the updated delivery event
    */
  override def deliveryUpdated(delivery: DeliveryEvent): Unit = {
    // ... do something with the event
  }

  /**
    *
    * @param driver - the updated driver event
    */
  override def driverUpdated(driver: DriverEvent): Unit = {
    // ... do something with the event
  }
}
```

```scala

// All your akka-http routes, including routes for Stuart Webhooks 
import akka.actor.typed.ActorSystem

import app.softnetwork.api.server.ApiRoutes

import app.softnetwork.stuart.serialization._

import org.json4s.Formats

trait MyStuartMainRoutes extends ApiRoutes with MyStuartWebHooks {
  override implicit def formats: Formats = stuartFormats

  override def apiRoutes: ActorSystem[_] => List[ApiRoute] = _ => List(MyStuartWebHooks)
}
```

```scala

// Your akka-http Application
import app.softnetwork.stuart.launch.StuartApi
import app.softnetwork.persistence.schema.InMemorySchemaProvider

object MyStuartApplication extends StuartApi with MyStuartMainRoutes with InMemorySchemaProvider{
  override lazy val config = akkaConfig
}
```

After launching `MyStuartApplication`, your Webhooks api will be accessible by default at http://localhost:8080/api/stuart/webhooks
