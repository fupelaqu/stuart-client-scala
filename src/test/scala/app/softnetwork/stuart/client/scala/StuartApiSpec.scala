package app.softnetwork.stuart.client.scala

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

  var id: Int = _

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

  "StuartApi" must {
    "validate address" in {
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
    "calculate pricing" in {
      Try(StuartApi().calculatePricing(request)) match {
        case Success(s) => s match {
          case Left(l) => fail()
          case Right(r) => logger.info(s"$r")
        }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "validate job" in {
      Try(StuartApi().validateJob(request)) match {
        case Success(s) => s match {
          case Left(l) => fail()
          case Right(r) => r.valid.getOrElse(false) shouldBe true
        }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "request job eta" in {
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
    "create job" in {
      Try(StuartApi().createJob(request)) match {
        case Success(s) =>
          s match {
            case Left(l) => fail()
            case Right(r) => id = r.id
          }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "load job" in {
      Try(StuartApi().loadJob(id)) match {
        case Success(s) =>
          s match {
            case Left(l) => fail()
            case Right(r) => logger.info(s"$r")
          }
        case Failure(f) => fail(f.getMessage)
      }
    }
    "cancel job" in {
      Try(StuartApi().cancelJob(id)) match {
        case Success(s) =>
          s match {
            case Left(l) => fail()
            case Right(_) =>
          }
        case Failure(f) => fail(f.getMessage)
      }
    }
  }
}
