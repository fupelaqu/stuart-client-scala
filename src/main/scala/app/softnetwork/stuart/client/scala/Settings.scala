package app.softnetwork.stuart.client.scala

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import configs.Configs

/**
  * Created by smanciot on 31/03/2021.
  */
object Settings extends StrictLogging {

  lazy val config: Config = ConfigFactory.load()

  lazy val StuartConfig: StuartApi.StuartConfig = Configs[StuartApi.StuartConfig].get(config, "stuart").toEither match{
    case Left(configError)  =>
      logger.error(s"Something went wrong with the provided arguments $configError")
      throw configError.configException
    case Right(stuartConfig) => stuartConfig
  }

}

