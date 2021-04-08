package app.softnetwork.stuart.client.scala

import java.util.UUID

import com.typesafe.scalalogging.StrictLogging

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import app.softnetwork.stuart.client.scala.message._
import app.softnetwork.stuart.client.scala.model._

import scala.util.{Success, Failure, Try}

/**
  * Created by smanciot on 31/03/2021.
  */
class StuartApiSpec extends AnyWordSpecLike with Matchers with StrictLogging {

  var job_id: Int = _

  var delivery_id: Int = _

  val client_reference = UUID.randomUUID().toString

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
      .withPickups(pickups)
      .withDropoffs(dropoffs)

  "StuartApi" must {
    "Validate address" in {
      StuartApi().validateAddress("12 rue rivoli, 75001 Paris") match {
        case Left(l) => fail()
        case Right(r) => r.success shouldBe true
      }
      Try(StuartApi().validateAddress("fake address")) match {
        case Success(s) =>
          s match {
            case Left(l) => logger.info(s"$l")
            case Right(r) => fail()
          }
        case Failure(f) => fail()
      }
    }
    "List zones per country" in {
      StuartApi().listZonesPerCountry("france").contains("paris") shouldBe true
    }
    "Check if a zone exists" in {
      StuartApi().checkZone("france", "Charleville-Mézières") shouldBe true
    }
    "Request a job pricing" in {
      Try(StuartApi().calculateShipping(request)) match {
        case Success(s) => s match {
          case Left(l) => fail()
          case Right(r) => logger.info(s"$r")
        }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "Validate job parameters" in {
      Try(StuartApi().validateJob(request)) match {
        case Success(s) => s match {
          case Left(l) => fail()
          case Right(r) => r.valid.getOrElse(false) shouldBe true
        }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "Request a job ETA" in {
      Try(StuartApi().eta(request)) match {
        case Success(s) => s match {
          case Left(l) => fail()
          case Right(r) => 
            logger.info(s"$r")
            r.eta >= 0 shouldBe true
        }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "Create a job" in {
      Try(StuartApi().createJob(request)) match {
        case Success(s) =>
          s match {
            case Left(l) => fail()
            case Right(r) =>
              job_id = r.id
              delivery_id = r.deliveries.head.id
          }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "Get a job listing" in {
      import JobStatus._
      val jobQuery = JobQuery.defaultInstance
        .withStatus(
          Seq(`new`, searching, in_progress)
        )
        .withPage(1)
        .withPerPage(10)
        .withClientReference(client_reference)
      Try(StuartApi().listJobs(jobQuery)) match {
        case Success(s) =>
          s match {
            case Left(l) => fail()
            case Right(r) =>
              r.nonEmpty shouldBe true
              r.exists(_.id == job_id) shouldBe true
          }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "Get a job" in {
      Try(StuartApi().getJob(job_id)) match {
        case Success(s) =>
          s match {
            case Left(l) => fail()
            case Right(r) => logger.info(s"$r")
          }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "Get driver's anonymous phone number" in {
      Try(StuartApi().getDriverPhoneNumber(delivery_id)) match {
        case Success(s) =>
          s match {
            case Left(l) =>
              logger.error(s"$l")
              l.error == "NO_CURRENT_DELIVERY" shouldBe true
            case Right(r) => logger.info(s"$r")
          }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "Update a job" in {
      val patch = JobPatch.defaultInstance.withDeliveries(
        Seq(DeliveryPatch.defaultInstance
          .withId(delivery_id.toString)
          .withPackageDescription("description")
        )
      )
      Try(StuartApi().updateJob(job_id, patch)) match {
        case Success(s) =>
          s match {
            case Left(l) => fail()
            case Right(_) =>
              val jobQuery = JobQuery.defaultInstance
                .withClientReference(client_reference)
                .withActive(true)
              Try(StuartApi().listJobs(jobQuery)) match {
                case Success(s2) =>
                  s2 match {
                    case Left(l) => fail()
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
                case Failure(f) => fail(f.getMessage)
              }
          }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "Cancel a job" in {
      Try(StuartApi().cancelJob(job_id)) match {
        case Success(s) =>
          s match {
            case Left(l) => fail()
            case Right(_) =>
              import JobStatus._
              val jobQuery = JobQuery.defaultInstance
                .withStatus(
                  Seq(canceled)
                )
                .withClientReference(client_reference)
              Try(StuartApi().listJobs(jobQuery)) match {
                case Success(s2) =>
                  s2 match {
                    case Left(l) => fail()
                    case Right(r) =>
                      r.nonEmpty shouldBe true
                      r.find(_.id == job_id) match {
                        case Some(j) => j.status match {
                          case _: canceled.type =>
                          case _ => fail()
                        }
                        case _ => fail()
                      }
                  }
                case Failure(f) => fail(f.getMessage)
              }
          }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "Cancel a delivery" in {
      Try(StuartApi().createJob(request)) match {
        case Success(s) =>
          s match {
            case Left(l) => fail()
            case Right(r) =>
              Try(StuartApi().cancelDelivery(r.deliveries.head.id)) match {
                case Success(s2) =>
                  s2 match {
                    case Left(l) =>
                      logger.error(s"$l")
                      fail()
                    case Right(_) =>
                  }
                case Failure(f) => fail(f.getMessage)
              }
          }
        case Failure(f) => fail(f.getMessage)
      }
    }
  }
}
