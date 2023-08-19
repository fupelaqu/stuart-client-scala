package app.softnetwork.stuart.server

import app.softnetwork.api.server.{ApiErrors, SwaggerApiEndpoint}
import app.softnetwork.stuart.config.Settings
import Settings.Config._
import akka.actor.typed.ActorSystem
import app.softnetwork.stuart.message.StuartGenericEvent.toStuartEvent
import app.softnetwork.stuart.message._
import app.softnetwork.stuart.serialization.stuartFormats
import org.json4s.Formats
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.generic.auto.SchemaDerivation
import sttp.tapir.Tapir
import sttp.tapir.server.ServerEndpoint

import scala.concurrent.{ExecutionContext, Future}

trait StuartWebHooksEndpoints
    extends SwaggerApiEndpoint
    with Tapir
    with SchemaDerivation
    with StuartCallBacks {

  import app.softnetwork.serialization._

  override implicit def formats: Formats = stuartFormats

  val webHooks: ServerEndpoint[AkkaStreams with WebSockets, Future] =
    endpoint
      .description("handle stuart WebHooks")
      .in(server.path / "webhooks")
      .in(header[Option[String]](server.authentication.header))
      .in(stringJsonBody.description("stuart event to handle"))
      .errorOut(ApiErrors.oneOfApiErrors)
      .serverLogic { case (key, raw) =>
        key match {
          case Some(key) if key == server.authentication.key =>
            val stuartEvent = toStuartEvent(
              serialization.read[StuartGenericEvent](raw)
            )
            stuartEvent.data match {
              case StuartEventData.Empty =>
                Future.successful(Left(ApiErrors.BadRequest("Invalid event")))
              case job: JobEvent =>
                stuartEvent.`type` match {
                  case Some("create") =>
                    jobCreated(job)
                    Future.successful(Right(()))
                  case Some("update") =>
                    jobUpdated(job)
                    Future.successful(Right(()))
                  case _ =>
                    Future.successful(Left(ApiErrors.BadRequest("Invalid job event")))
                }
              case delivery: DeliveryEvent =>
                stuartEvent.`type` match {
                  case Some("create") =>
                    deliveryCreated(delivery)
                    Future.successful(Right(()))
                  case Some("update") =>
                    deliveryUpdated(delivery)
                    Future.successful(Right(()))
                  case _ =>
                    Future.successful(Left(ApiErrors.BadRequest("Invalid delivery event")))
                }
              case driver: DriverEvent =>
                stuartEvent.`type` match {
                  case Some("update") =>
                    driverUpdated(driver)
                    Future.successful(Right(()))
                  case _ =>
                    Future.successful(Left(ApiErrors.BadRequest("Invalid driver event")))
                }
              case _ =>
                Future.successful(Left(ApiErrors.BadRequest("Invalid event")))
            }
          case _ => Future.successful(Left(ApiErrors.Unauthorized("Invalid key")))
        }
      }

  override def endpoints: List[ServerEndpoint[AkkaStreams with WebSockets, Future]] =
    List(
      webHooks
    )
}

object StuartWebHooksEndpoints {
  def apply(aSystem: ActorSystem[_]): StuartWebHooksEndpoints =
    new StuartWebHooksEndpoints {
      override implicit def ec: ExecutionContext = aSystem.executionContext
    }
}
