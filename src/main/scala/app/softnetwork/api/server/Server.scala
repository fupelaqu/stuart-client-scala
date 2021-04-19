package app.softnetwork.api.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import configs.Configs

import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

/**
  * Created by smanciot on 25/04/2020.
  */
trait Server {

  import Server._

  def routes: Route

  implicit def system: ActorSystem

  private lazy val shutdown = CoordinatedShutdown(system)

  implicit lazy val executionContext = system.dispatcher

  def start(): Unit = {
    Http().bindAndHandle(routes, Settings.Interface, Settings.Port).onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info(
          s"${system.name} application started at http://{}:{}/",
          address.getHostString,
          address.getPort
        )

        shutdown.addTask(CoordinatedShutdown.PhaseServiceRequestsDone, "http-graceful-terminate") { () =>
          binding.terminate(Settings.DefaultTimeout).map { _ =>
            system.log.info(
              s"${system.name} application http://{}:{}/ graceful shutdown completed",
              address.getHostString,
              address.getPort
            )
            Done
          }
        }
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
}

object Server{
  def apply(aroute: Route)(implicit asystem: ActorSystem): Server = {
    new Server(){
      override val routes = aroute
      implicit val system = asystem
    }
  }

  object Settings extends StrictLogging {
    lazy val config: Config = ConfigFactory.load()

    lazy val Interface = config.getString("akka.http.server.interface")

    lazy val Port = config.getInt("akka.http.server.port")

    lazy val DefaultTimeout = Configs[FiniteDuration].get(config, "akka.http.server.request-timeout").toEither match{
      case Left(configError)  =>
        logger.error(s"Something went wrong with the provided arguments $configError")
        throw configError.configException
      case Right(timeout) => timeout
    }
  }
}
