package app.softnetwork.stuart.client.scala

import org.json4s.Formats

import app.softnetwork.api.client.serialization._

/**
  * Created by smanciot on 31/03/2021.
  */
package object serialization {

  val stuartFormats: Formats = defaultFormats ++ StuartPBSerializers.all
}
