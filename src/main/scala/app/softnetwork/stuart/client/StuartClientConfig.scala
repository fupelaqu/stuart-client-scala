package app.softnetwork.stuart.client

import app.softnetwork.api.client.auth.Oauth2ApiConfig

/**
  * Created by smanciot on 19/04/2021.
  */
case class StuartClientConfig(dryRun: Boolean,
                              apiClientId: String,
                              apiSecret: String,
                              oauth2Api: String = "/oauth/token",
                              zones: Map[String, Seq[String]],
                              tax: Int = 20) extends Oauth2ApiConfig {
  lazy val baseUrl = {
    if(dryRun){
      "https://api.sandbox.stuart.com"
    }
    else{
      "https://api.stuart.com"
    }
  }

}
