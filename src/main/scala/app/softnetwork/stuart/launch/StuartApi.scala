package app.softnetwork.stuart.launch

import akka.actor.typed.ActorSystem
import app.softnetwork.api.server.ApiServer
import app.softnetwork.build.info.stuart.client.scala.BuildInfo
import app.softnetwork.persistence.schema.{InMemorySchemaProvider, SchemaProvider}
import app.softnetwork.stuart.server.StuartMainRoutes
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}

/** Created by smanciot on 19/04/2021.
  */
trait StuartApi extends ApiServer with StuartMainRoutes {

  lazy val log: Logger = LoggerFactory getLogger getClass.getName

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

  override def schemaProvider: ActorSystem[_] => SchemaProvider = _ => new InMemorySchemaProvider {}

  lazy val akkaConfig: Config =
    ConfigFactory
      .parseString("""
      |  akka{
      |    discovery{
      |      method = config
      |      config.services = {
      |        local = {
      |          endpoints = [
      |            {
      |              host = "127.0.0.1"
      |              port = 8558
      |            }
      |          ]
      |        }
      |      }
      |    }
      |    management {
      |      http {
      |        hostname = "127.0.0.1"
      |        port = 8558
      |      }
      |    }
      |  }
      |
      |""".stripMargin)
      .withFallback(ConfigFactory.load("softnetwork-in-memory-persistence.conf"))
      .withFallback(ConfigFactory.load("softnetwork-api-server.conf"))
      .withFallback(ConfigFactory.load())

}
