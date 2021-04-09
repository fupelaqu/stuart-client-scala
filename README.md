# stuart-client-scala

A client for Stuart API written in scala which relies on [generic-client-api](https://github.com/fupelaqu/generic-client-api) and [Protocol buffers](https://developers.google.com/protocol-buffers/) using [ScalaPB compiler](https://scalapb.github.io/)

For a complete documentation of all endpoints offered by the Stuart API, you can visit [Stuart API documentation](https://stuart.api-docs.io).

## Installation

```scala
resolvers += "Artifactory" at "https://softnetwork.jfrog.io/artifactory/releases/"

libraryDependencies += "app.softnetwork.stuart" %% "stuart-client-scala" % "0.1.2"
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

StuartApi().calculateShipping(request) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: ShippingCalculated) => // eg ShippingCalculated(amount = 17, currency = EUR)
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
    Seq(`new`, searching, in_progress, scheduled)
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
StuartApi().getJob(job_id) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: Job) => // ... do something with Job
}
```

### Get driver's anonymous phone number

```scala
StuartApi().getDriverPhoneNumber(delivery_id) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(r: DriverPhoneNumber) => // ... do something with DriverPhoneNumber
}
```

### Update a job

```scala
val patch = JobPatch.defaultInstance.withDeliveries(
  Seq(DeliveryPatch.defaultInstance
    .withId(idDelivery)
    .withPackageDescription("description")
  )
)

StuartApi().updateJob(job_id, patch) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(_) => // ... do something
}
```

### Cancel a job

```scala
StuartApi().cancelJob(job_id) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(_) => // ... do something
}
```

### Cancel a delivery

```scala
StuartApi().cancelDelivery(delivery_id) match {
  case Left(l: StuartError) => // ... do something with StuartError 
  case Right(_) => // ... do something
}
```
