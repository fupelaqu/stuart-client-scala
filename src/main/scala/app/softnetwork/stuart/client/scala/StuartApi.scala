package app.softnetwork.stuart.client.scala

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods

import akka.stream.Materializer
import app.softnetwork.api.client.auth.{Oauth2ApiConfig, Oauth2Authenticator}

import org.json4s.Formats

import app.softnetwork.api.client.GenericApi

import app.softnetwork.stuart.client.scala.message._
import app.softnetwork.stuart.client.scala.model._
import app.softnetwork.stuart.client.scala.serialization._

import scala.language.implicitConversions

/**
  * Created by smanciot on 31/03/2021.
  */
sealed trait StuartApi extends GenericApi with Oauth2Authenticator with StuartAddressApi with StuartJobApi{
  override implicit def formats: Formats = stuartFormats
}

trait StuartAddressApi {_: StuartApi =>

  def validateAddress(address: String, picking: Boolean = true): Either[StuartError, AddressValidated] = {
    val `type` =
      if(picking){
        "picking"
      }
      else {
        "delivering"
      }
    doGet[AddressValidated, StuartError](
      "/v2/addresses/validate",
      Map("address" -> address, "type" -> `type`)
    )
  }

}

trait StuartJobApi {_: StuartApi =>

  def calculatePricing(job: JobRequest): Either[StuartError, PricingCalculated] = {
    doPost[CalculatePricing, PricingCalculated, StuartError](
      "/v2/jobs/pricing",
      CalculatePricing(job)
    )
  }

  def validateJob(job: JobRequest): Either[StuartError, JobValidated] = {
    doPost[ValidateJob, JobValidated, StuartError](
      "/v2/jobs/validate",
      ValidateJob(job)
    )
  }

  def eta(job: JobRequest): Either[StuartError, JobEta] = {
    doPost[RequestJobEta, JobEta, StuartError](
      "/v2/jobs/eta",
      RequestJobEta(job)
    )
  }

  def createJob(job: JobRequest): Either[StuartError, Job] = {
    doPost[CreateJob, Job, StuartError](
      "/v2/jobs",
      CreateJob(job)
    )
  }

  def loadJob(id: Int): Either[StuartError, Job] = {
    doGet[Job, StuartError](s"/v2/jobs/$id")
  }

  def cancelJob(id: Int): Either[StuartError, Unit] = {
    executeWithoutRequestAndResponse[StuartError](
      s"/v2/jobs/$id/cancel",
      HttpMethods.POST
    )
  }
}

object StuartApi {

  private[this] var instance: Option[StuartApi] = None

  def apply(sys: ActorSystem = ActorSystem()): StuartApi = {
    instance match {
      case Some(s) => s
      case _ =>
        val api = new StuartApi {
          implicit val system = sys
          implicit val ec = system.dispatcher
          implicit val mat = Materializer(system)
          override lazy val config = Settings.StuartConfig
        }
        instance = Some(api)
        api
    }
  }

  case class Config(dryRun: Boolean, apiClientId: String, apiSecret: String, oauth2Api: String = "/oauth/token")
    extends Oauth2ApiConfig {
    lazy val baseUrl = {
      if(dryRun){
        "https://api.sandbox.stuart.com"
      }
      else{
        "https://api.stuart.com"
      }
    }

  }

}
