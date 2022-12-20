package app.softnetwork.stuart.launch

import app.softnetwork.api.server.launch.Application
import app.softnetwork.build.info.stuart.client.scala.BuildInfo
import app.softnetwork.persistence.query.InMemorySchemaProvider

import app.softnetwork.stuart.server.StuartMainRoutes

/** Created by smanciot on 19/04/2021.
  */
object StuartApplication extends Application with StuartMainRoutes with InMemorySchemaProvider {

  /** @return
    *   the banner to print to the console
    */
  override def banner: String =
    """
      | ____  _                    _
      |/ ___|| |_ _   _  __ _ _ __| |_
      |\___ \| __| | | |/ _` | '__| __|
      | ___) | |_| |_| | (_| | |  | |_
      ||____/ \__|\__,_|\__,_|_|   \__|
      |
      |
    """.stripMargin

  override def systemVersion(): String = BuildInfo.version
}
