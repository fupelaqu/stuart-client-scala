package app.softnetwork.stuart.launch

import app.softnetwork.api.server.launch.Application

object StuartApplication extends Application with StuartApi {
  override lazy val config = akkaConfig
}
