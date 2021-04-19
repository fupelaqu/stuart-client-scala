package app.softnetwork.stuart

import app.softnetwork.stuart.client.StuartClientConfig
import app.softnetwork.stuart.server.StuartServerConfig
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import configs.Configs

/**
  * Created by smanciot on 31/03/2021.
  */
object Settings extends StrictLogging {

  private[this] lazy val config: Config = ConfigFactory.load()

  lazy val Config: StuartConfig = Configs[StuartConfig].get(config, "stuart").toEither match{
    case Left(configError)  =>
      logger.error(s"Something went wrong with the provided arguments $configError")
      throw configError.configException
    case Right(stuartConfig) => stuartConfig
  }

  case class StuartConfig(client: StuartClientConfig, server: StuartServerConfig)
}

