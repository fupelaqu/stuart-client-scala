package app.softnetwork.api.server

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{StatusCodes, HttpResponse}
import akka.http.scaladsl.server.{Directives, Route}
import com.typesafe.scalalogging.StrictLogging

import scala.util.{Failure, Success, Try}

/**
  * Created by smanciot on 19/04/2021.
  */
trait MainRoutes extends StrictLogging {_: Directives =>

  final def mainRoutes: ActorSystem => Route = system => {
    pathPrefix("api"){
      Try(
        apiRoutes
      ) match {
        case Success(s) => s
        case Failure(f) =>
          logger.error(f.getMessage, f.getCause)
          complete(
            HttpResponse(
              StatusCodes.InternalServerError,
              entity = f.getMessage
            )
          )
      }
    }
  }

  def apiRoutes: Route
}
