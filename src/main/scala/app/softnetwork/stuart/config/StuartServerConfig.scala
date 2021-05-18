package app.softnetwork.stuart.config

/**
  * Created by smanciot on 19/04/2021.
  */
case class StuartServerConfig(path: String, authentication: StuartWebhookAuthentication)

case class StuartWebhookAuthentication(header: String, key: String)
