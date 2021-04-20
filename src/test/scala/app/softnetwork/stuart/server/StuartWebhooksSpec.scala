package app.softnetwork.stuart.server

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}

import app.softnetwork.stuart.Settings.Config
import app.softnetwork.stuart.message.{DeliveryEvent, DriverEvent, JobEvent, StuartGenericEvent}

import com.typesafe.scalalogging.StrictLogging

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._

/**
  * Created by smanciot on 19/04/2021.
  */
class StuartWebHooksSpec extends StuartWebHooks
  with AnyWordSpecLike
  with Matchers
  with ScalatestRouteTest
  with StrictLogging {

  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(2.seconds)

  val jobId = 45153

  val clientReference = "order_id_12345"

  val deliveryId = 40567

  val jobCreated =
    s"""
      |{
      |  "event": "job",
      |  "type": "create",
      |  "data": {
      |    "id": $jobId,
      |    "currentDelivery": null,
      |    "transportType": null,
      |    "status": "scheduled",
      |    "comment": null,
      |    "originComment": null,
      |    "destinationComment": null,
      |    "jobReference": "$clientReference"
      |  }
      |}
    """.stripMargin

  val jobUpdated =
    s"""
      |{
      |  "event": "job",
      |  "type": "update",
      |  "data": {
      |    "id": $jobId,
      |    "currentDelivery": {
      |      "id": $deliveryId,
      |      "driver": {
      |        "status": "on_duty",
      |        "latitude": 48.85349,
      |        "longitude": 2.37467,
      |        "name": "Durward Greenfelder",
      |        "firstname": "Evan",
      |        "lastname": "Koepp",
      |        "phone": "+33120510321",
      |        "picture_path_imgix": null,
      |        "transportType": {
      |          "code": "motorbike"
      |        }
      |      },
      |      "transportType": {
      |        "code": "motorbike"
      |      },
      |      "etaToDestination": "2017-03-15T16:03:48.000+01:00",
      |      "etaToOrigin": "2017-03-15T15:38:50.000+01:00",
      |      "status": "delivered"
      |    },
      |    "transportType": {
      |      "code": "motorbike"
      |    },
      |    "status": "finished",
      |    "comment": null,
      |    "originComment": null,
      |    "destinationComment": null,
      |    "jobReference": "$clientReference"
      |  }
      |}
    """.stripMargin

  val deliveryCreated =
    s"""
      |{
      |  "event": "delivery",
      |  "type": "create",
      |  "data": {
      |    "id": $deliveryId,
      |    "driver": null,
      |    "transportType": {
      |      "code": "bike"
      |    },
      |    "packageType": {
      |      "code": "small"
      |    },
      |    "etaToDestination": null,
      |    "etaToOrigin": null,
      |    "status": "pending",
      |    "trackingUrl": "https://stuart.followmy.delivery/{DeliveryId}/{Hash}",
      |    "clientReference": null
      |  }
      |}
    """.stripMargin

  val deliveryUpdated =
    s"""
      |{
      |  "event": "delivery",
      |  "type": "update",
      |  "data": {
      |    "id": $deliveryId,
      |    "driver": {
      |      "status": "busy",
      |      "latitude": 48.83868,
      |      "longitude": 2.3998,
      |      "name": "Prof Zoe Garcia",
      |      "firstname": "Enzo",
      |      "lastname": "Caron",
      |      "phone": "+33962156964",
      |      "picture_path_imgix": null,
      |      "transportType": {
      |        "code": "bike"
      |      }
      |    },
      |    "transportType": {
      |      "code": "bike"
      |    },
      |    "packageType": {
      |      "code": "small"
      |    },
      |    "etaToDestination": "2017-09-13T10:46:03.000+02:00",
      |    "etaToOrigin": "2017-09-13T10:41:01.000+02:00",
      |    "status": "delivering",
      |    "clientReference": "$clientReference",
      |    "trackingUrl": "https://stuart.followmy.delivery/{DeliveryId}/{Hash}"
      |  }
      |}
      |""".stripMargin

  val driverUpdated =
    s"""
      |{
      |  "event": "driver",
      |  "type": "update",
      |  "data": {
      |    "status": "busy",
      |    "latitude": 48.85559,
      |    "longitude": 2.36014,
      |    "name": "Maverick Lang",
      |    "firstname": "Brannon",
      |    "lastname": "Marks",
      |    "phone": "+33957261331",
      |    "picture_path_imgix": null,
      |    "transportType": {
      |      "code": "car"
      |    },
      |    "job": {
      |      "id": $jobId,
      |      "currentDelivery": {
      |        "id": $deliveryId,
      |        "driver": {
      |          "status": "busy",
      |          "latitude": 48.85559,
      |          "longitude": 2.36014,
      |          "name": "Maverick Lang",
      |          "firstname": "Brannon",
      |          "lastname": "Marks",
      |          "phone": "+33957261331",
      |          "picture_path_imgix": null,
      |          "transportType": {
      |            "code": "car"
      |          }
      |        },
      |        "transportType": {
      |          "code": "car"
      |        },
      |        "packageType": {
      |          "code": "small"
      |        },
      |        "etaToDestination": "2018-07-24T16:55:12.000+02:00",
      |        "trackingUrl": "https://sandbox.followmy.delivery/103xxx/2a2d...xxx",
      |        "etaToOrigin": "2018-07-24T16:43:56.000+02:00",
      |        "status": "delivering",
      |        "clientReference": "Order_ID#1234_1"
      |      },
      |      "transportType": {
      |        "code": "car"
      |      },
      |      "packageType": {
      |        "code": "xlarge"
      |      },
      |      "status": "in_progress",
      |      "comment": null,
      |      "pickupAt": null,
      |      "dropoffAt": null,
      |      "createdAt": "2018-07-24T16:43:44.000+02:00",
      |      "endedAt": null,
      |      "originComment": null,
      |      "destinationComment": null,
      |      "jobReference": "$clientReference"
      |    }
      |  }
      |}
    """.stripMargin

  val header = RawHeader(Config.server.authentication.header, Config.server.authentication.key)

  "Stuart Web Hooks" should {
    "handle job created event" in {
      testRoute(jobCreated)
    }
    "handle job updated event" in {
      testRoute(jobUpdated)
    }
    "handle delivery created event" in {
      testRoute(deliveryCreated)
    }
    "handle delivery updated event" in {
      testRoute(deliveryUpdated)
    }
    "handle driver updated event" in {
      testRoute(driverUpdated)
    }
  }

  /**
    *
    * @param job - the created job event
    */
  override def jobCreated(job: JobEvent): Unit = {
    assert(job.id.getOrElse(0) == jobId)
    assert(job.jobReference.getOrElse("") == clientReference)
  }

  /**
    *
    * @param job - the updated job event
    */
  override def jobUpdated(job: JobEvent): Unit = {
    assert(job.id.getOrElse(0) == jobId)
    assert(job.jobReference.getOrElse("") == clientReference)
    assert(job.currentDelivery.flatMap(_.id).getOrElse(0) == deliveryId)
  }


  /**
    *
    * @param delivery - the created delivery event
    */
  override def deliveryCreated(delivery: DeliveryEvent): Unit = {
    assert(delivery.id.getOrElse(0) == deliveryId)
  }

  /**
    *
    * @param delivery - the updated delivery event
    */
  override def deliveryUpdated(delivery: DeliveryEvent): Unit = {
    assert(delivery.id.getOrElse(0) == deliveryId)
    assert(delivery.clientReference.getOrElse("") == clientReference)
  }

  /**
    *
    * @param driver - the updated driver event
    */
  override def driverUpdated(driver: DriverEvent): Unit = {
    assert(driver.job.flatMap(_.id).getOrElse(0) == jobId)
    assert(driver.job.flatMap(_.jobReference).getOrElse("") == clientReference)
    assert(driver.job.flatMap(_.currentDelivery.flatMap(_.id)).getOrElse(0) == deliveryId)
  }

  protected def testRoute(input: String) = {
    Post(s"/${Config.server.path}/webhooks", serialization.read[StuartGenericEvent](input)
    ).withHeaders(header) ~> stuartRoutes ~> check {
      status shouldEqual StatusCodes.OK
    }
  }
}
