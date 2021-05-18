package app.softnetwork.stuart.config

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import configs.Configs

/**
  * Created by smanciot on 31/03/2021.
  */
object Settings extends StrictLogging {

  private[this] lazy val config: Config = ConfigFactory.load().withFallback(ConfigFactory.load("stuart-api.conf"))

  lazy val Config: StuartConfig = Configs[StuartConfig].get(config, "stuart").toEither match{
    case Left(configError)  =>
      logger.error(s"Something went wrong with the provided arguments $configError")
      throw configError.configException
    case Right(stuartConfig) => stuartConfig
  }

  case class StuartConfig(client: StuartClientConfig, server: StuartServerConfig)
}

