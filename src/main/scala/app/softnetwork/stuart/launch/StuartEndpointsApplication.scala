package app.softnetwork.stuart.launch

import app.softnetwork.api.server.launch.Application
import app.softnetwork.persistence.schema.InMemorySchemaProvider
import app.softnetwork.stuart.server.StuartMainEndpoints

object StuartEndpointsApplication
    extends Application
    with StuartApi
    with StuartMainEndpoints
    with InMemorySchemaProvider {
  override lazy val config = akkaConfig
}
