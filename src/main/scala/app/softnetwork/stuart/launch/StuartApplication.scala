package app.softnetwork.stuart.launch

import app.softnetwork.api.server.launch.Application
import app.softnetwork.persistence.schema.InMemorySchemaProvider

object StuartApplication extends Application with StuartApi with InMemorySchemaProvider {
  override lazy val config = akkaConfig
}
