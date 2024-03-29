package app.softnetwork.stuart.launch

import app.softnetwork.api.server.launch.Application
import app.softnetwork.persistence.schema.InMemorySchemaProvider
import app.softnetwork.stuart.server.StuartMainRoutes

object StuartApplication
    extends Application
    with StuartApi
    with StuartMainRoutes
    with InMemorySchemaProvider {
  override lazy val config = akkaConfig
}
