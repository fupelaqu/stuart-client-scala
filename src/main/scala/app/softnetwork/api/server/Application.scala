package app.softnetwork.api.server

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route

/**
  * Created by smanciot on 19/04/2021.
  */
trait Application extends App {_: MainRoutes =>

  /**
    *
    * initialize all application routes
    *
    */
  def routes: ActorSystem => Route = system => mainRoutes(system)


  /**
    *
    * @return the banner to print to the console
    */
  def banner: String =
    """
      | ____         __ _              _                      _
      |/ ___|  ___  / _| |_ _ __   ___| |___      _____  _ __| | __
      |\___ \ / _ \| |_| __| '_ \ / _ \ __\ \ /\ / / _ \| '__| |/ /
      | ___) | (_) |  _| |_| | | |  __/ |_ \ V  V / (_) | |  |   <
      ||____/ \___/|_|  \__|_| |_|\___|\__| \_/\_/ \___/|_|  |_|\_\
      |
      |""".stripMargin


  implicit def system: ActorSystem = ActorSystem()

  implicit val executionContext = system.dispatcher

  // print a cool banner
  println(banner)

  // start the server
  Server(routes(system)).start()
}
