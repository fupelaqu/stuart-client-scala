package app.softnetwork.stuart.server

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server._
import app.softnetwork.stuart.config.Settings
import Settings.Config._
import app.softnetwork.stuart.message._
import app.softnetwork.stuart.serialization._
import com.typesafe.scalalogging.StrictLogging
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.jackson.Serialization
import org.json4s.{jackson, Formats}

/** Created by smanciot on 19/04/2021.
  */
trait StuartWebHooks extends Directives with Json4sSupport with StrictLogging {

  implicit def formats: Formats = stuartFormats

  implicit def serialization: Serialization.type = jackson.Serialization

  implicit def exceptionHandler: ExceptionHandler =
    ExceptionHandler { case t: Throwable =>
      extractUri { uri =>
        logger.error(s"Request to $uri could not be handled normally")
        complete(HttpResponse(StatusCodes.InternalServerError, entity = t.getMessage))
      }
    }

  val stuartRoutes: Route = {
    pathPrefix(server.path) {
      healthcheck ~ webhooks
    }
  }

  lazy val healthcheck: Route = path("healthcheck") {
    get {
      complete(StatusCodes.OK)
    }
  }

  lazy val webhooks: Route = path("webhooks") {
    post {
      optionalHeaderValueByName(server.authentication.header) {
        case Some(key) if key == server.authentication.key =>
          entity(as[StuartGenericEvent]) { stuartGenericEvent =>
            val stuartEvent: StuartEvent = stuartGenericEvent
            stuartEvent.data match {
              case StuartEventData.Empty =>
                complete(
                  HttpResponse(StatusCodes.BadRequest, entity = serialization.write(stuartEvent))
                )
              case job: JobEvent =>
                stuartEvent.`type` match {
                  case Some("create") =>
                    jobCreated(job)
                    complete(HttpResponse(StatusCodes.OK))
                  case Some("update") =>
                    jobUpdated(job)
                    complete(HttpResponse(StatusCodes.OK))
                  case _ =>
                    complete(
                      HttpResponse(
                        StatusCodes.BadRequest,
                        entity = serialization.write(stuartEvent)
                      )
                    )
                }
              case delivery: DeliveryEvent =>
                stuartEvent.`type` match {
                  case Some("create") =>
                    deliveryCreated(delivery)
                    complete(HttpResponse(StatusCodes.OK))
                  case Some("update") =>
                    deliveryUpdated(delivery)
                    complete(HttpResponse(StatusCodes.OK))
                  case _ =>
                    complete(
                      HttpResponse(
                        StatusCodes.BadRequest,
                        entity = serialization.write(stuartEvent)
                      )
                    )
                }
              case driver: DriverEvent =>
                stuartEvent.`type` match {
                  case Some("update") =>
                    driverUpdated(driver)
                    complete(HttpResponse(StatusCodes.OK))
                  case _ =>
                    complete(
                      HttpResponse(
                        StatusCodes.BadRequest,
                        entity = serialization.write(stuartEvent)
                      )
                    )
                }
              case _ =>
                complete(
                  HttpResponse(StatusCodes.BadRequest, entity = serialization.write(stuartEvent))
                )
            }

          }
        case _ => complete(HttpResponse(StatusCodes.Unauthorized))
      }
    }
  }

  /** @param job
    *   - the created job event
    */
  def jobCreated(job: JobEvent): Unit = ()

  /** @param job
    *   - the updated job event
    */
  def jobUpdated(job: JobEvent): Unit = ()

  /** @param delivery
    *   - the created delivery event
    */
  def deliveryCreated(delivery: DeliveryEvent): Unit = ()

  /** @param delivery
    *   - the updated delivery event
    */
  def deliveryUpdated(delivery: DeliveryEvent): Unit = ()

  /** @param driver
    *   - the updated driver event
    */
  def driverUpdated(driver: DriverEvent): Unit = ()

}

object StuartWebHooks extends StuartWebHooks
