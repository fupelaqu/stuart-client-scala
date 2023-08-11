package app.softnetwork.stuart.server

import akka.actor.typed.ActorSystem
import app.softnetwork.api.server.{ApiRoute, ApiRoutes}
import app.softnetwork.stuart.serialization._
import org.json4s.Formats

/** Created by smanciot on 19/04/2021.
  */
trait StuartMainRoutes extends ApiRoutes {

  override implicit def formats: Formats = stuartFormats

  override def apiRoutes: ActorSystem[_] => List[ApiRoute] = _ => List(StuartWebHooks)
}
