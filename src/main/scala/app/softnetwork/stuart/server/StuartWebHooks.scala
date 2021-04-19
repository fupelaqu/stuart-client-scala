package app.softnetwork.stuart.server

import akka.http.scaladsl.model.{StatusCodes, HttpResponse}
import akka.http.scaladsl.server.Directives
import app.softnetwork.stuart.Settings.Config

/**
  * Created by smanciot on 19/04/2021.
  */
trait StuartWebHooks extends Directives {

  val route = {
    pathPrefix(Config.server.path){
      pathEnd{
        post{
          optionalHeaderValueByName(Config.server.authentication.header){
            case Some(key) if key == Config.server.authentication.key =>
              ???
            case _ => complete(HttpResponse(StatusCodes.Unauthorized))
          }
        }
      }
    }
  }

}
