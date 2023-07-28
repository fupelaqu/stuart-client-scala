package app.softnetwork.stuart.server

import akka.actor.typed.ActorSystem
import app.softnetwork.api.server.{ApiEndpoint, ApiEndpoints}
import app.softnetwork.stuart.serialization.stuartFormats
import org.json4s.Formats

trait StuartMainEndpoints extends ApiEndpoints {
  override implicit def formats: Formats = stuartFormats
  override def endpoints: ActorSystem[_] => List[ApiEndpoint] = system =>
    List(StuartWebHooksEndpoints(system))
}
