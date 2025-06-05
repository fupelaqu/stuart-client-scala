package app.softnetwork.stuart.client

import java.time.ZonedDateTime
import java.util.UUID

import com.typesafe.scalalogging.StrictLogging

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import app.softnetwork.stuart.message._
import app.softnetwork.stuart.model._

import app.softnetwork.api.client.ApiCompletion._

/** Created by smanciot on 31/03/2021.
  */
class StuartApiSpec extends AnyWordSpecLike with Matchers with StrictLogging {

  var job_id: Int = _

  var delivery_id: Int = _

  val client_reference: String = UUID.randomUUID().toString

  val pickups: List[Pickup] = List(
    Pickup.defaultInstance
      .withAddress("12 rue rivoli, 75001 Paris")
      .withContact(
        ContactRequest.defaultInstance
          .withFirstname("Bobby")
          .withLastname("Brown")
          .withPhone("+33610101010")
      )
  )
  val dropoffs: List[DropOff] = List(
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
  val request: JobRequest =
    JobRequest.defaultInstance
      .withTransportType(TransportType.bike)
      // can not schedule a dropoff in less than 60 minutes from now
      .withPickupAt(ZonedDateTime.now().plusHours(1).plusMinutes(1))
//      .withDropoffAt(ZonedDateTime.now().plusHours(1).plusMinutes(1))
      .withPickups(pickups)
      .withDropoffs(dropoffs)

  "StuartApi" must {
    "Validate address" in {
      StuartApi().validateAddress("12 rue rivoli, 75001 Paris") sync {
        case Left(_)  => fail()
        case Right(r) => r.success shouldBe true
      }
      StuartApi().validateAddress("fake address") sync {
        case Left(l) =>
          logger.info(s"$l")
          succeed
        case Right(_) => fail()
      }
    }
    "List zones per country" in {
      StuartApi().listZonesPerCountry("france").contains("paris") shouldBe true
    }
    "Check if a zone exists" in {
      StuartApi().checkZone("france", "Charleville-Mézières") shouldBe true
    }
    "Request a job pricing" in {
      StuartApi().calculatePricing(request) sync {
        case Left(l) =>
          logger.error(s"$l")
          fail(l.message)
        case Right(r) =>
          logger.info(s"$r")
          succeed
      }
    }
    "Validate job parameters" in {
      StuartApi().validateJob(request) sync {
        case Left(l) =>
          logger.error(s"$l")
          fail(l.message)
        case Right(r) => r.valid.getOrElse(false) shouldBe true
      }
    }
    "Request a job ETA to pickup" in {
      StuartApi().eta(request) sync {
        case Left(l) =>
          logger.error(s"$l")
          fail(l.message)
        case Right(r) =>
          logger.info(s"$r")
          r.eta >= 0 shouldBe true
      }
    }
    "Request a job ETA to dropoff" in {
      StuartApi().cpt(request) sync {
        case Left(l) =>
          logger.error(s"$l")
          fail(l.message)
        case Right(r) =>
          logger.info(s"$r")
          r.seconds >= 0 shouldBe true
      }
    }
    "Create a job" in {
      StuartApi().createJob(request) sync {
        case Left(l) =>
          logger.error(s"$l")
          fail(l.message)
        case Right(r) =>
          job_id = r.id
          delivery_id = r.deliveries.head.id
          succeed
      }
    }
    "Get a job listing" in {
      import JobStatus._
      val jobQuery = JobQuery.defaultInstance
        .withStatus(
          Seq(`new`, searching, in_progress, scheduled)
        )
        .withPage(1)
        .withPerPage(10)
        .withClientReference(client_reference)
      StuartApi().listJobs(jobQuery) sync {
        case Left(l) =>
          logger.error(s"$l")
          fail(l.message)
        case Right(r) =>
          r.nonEmpty shouldBe true
          r.exists(_.id == job_id) shouldBe true
      }
    }
    "Get a job" in {
      StuartApi().getJob(s"$job_id") sync {
        case Left(l) =>
          logger.error(s"$l")
          fail(l.message)
        case Right(r) =>
          logger.info(s"$r")
          succeed
      }
    }
    "Get driver's anonymous phone number" in {
      StuartApi().getDriverPhoneNumber(s"$delivery_id") sync {
        case Left(l) =>
          logger.error(s"$l")
          l.error == "NO_CURRENT_DELIVERY" shouldBe true
        case Right(r) =>
          logger.info(s"$r")
          succeed
      }
    }
    "Update a job" in {
      val patch = JobPatch.defaultInstance.withDeliveries(
        Seq(
          DeliveryPatch.defaultInstance
            .withId(delivery_id.toString)
            .withPackageDescription("description")
        )
      )
      StuartApi().updateJob(s"$job_id", patch) sync {
        case Left(l) =>
          logger.error(s"$l")
          fail(l.message)
        case Right(_) =>
          import JobStatus._
          val jobQuery = JobQuery.defaultInstance
            .withClientReference(client_reference)
            .withStatus(
              Seq(`new`, searching, in_progress, scheduled)
            )
          StuartApi().listJobs(jobQuery) sync {
            case Left(l) =>
              logger.error(s"$l")
              fail(l.message)
            case Right(r) =>
              r.nonEmpty shouldBe true
              r.find(_.id == job_id) match {
                case Some(j) =>
                  j.deliveries.find(_.id == delivery_id) match {
                    case Some(d) =>
                      d.package_description shouldBe patch.deliveries.head.package_description
                    case _ => fail()
                  }
                case _ => fail()
              }
          }
      }
    }
    "Cancel a job" in {
      StuartApi().cancelJob(s"$job_id") sync {
        case Left(l) =>
          logger.error(s"$l")
          fail(l.message)
        case Right(_) =>
          import JobStatus._
          val jobQuery = JobQuery.defaultInstance
            .withStatus(
              Seq(canceled)
            )
            .withClientReference(client_reference)
          StuartApi().listJobs(jobQuery) sync {
            case Left(l) =>
              logger.error(s"$l")
              fail(l.message)
            case Right(r) =>
              r.nonEmpty shouldBe true
              r.find(_.id == job_id) match {
                case Some(j) =>
                  j.status match {
                    case _: canceled.type => succeed
                    case _                => fail()
                  }
                case _ => fail()
              }
          }
      }
    }
    "Cancel a delivery" in {
      StuartApi().createJob(request) sync {
        case Left(l) =>
          logger.error(s"$l")
          fail(l.message)
        case Right(r) =>
          StuartApi().cancelDelivery(s"${r.deliveries.head.id}") sync {
            case Left(l) =>
              logger.error(s"$l")
              fail()
            case Right(_) => succeed
          }
      }
    }
  }
}
