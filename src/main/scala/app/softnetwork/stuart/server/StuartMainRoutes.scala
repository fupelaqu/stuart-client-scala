package app.softnetwork.stuart.server

import app.softnetwork.api.server.MainRoutes

/**
  * Created by smanciot on 19/04/2021.
  */
trait StuartMainRoutes extends MainRoutes with StuartWebHooks {

  lazy val apiRoutes = stuartRoutes

}
