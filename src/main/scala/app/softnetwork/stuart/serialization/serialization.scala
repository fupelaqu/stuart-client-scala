package app.softnetwork.stuart

import app.softnetwork.serialization._

import org.json4s.Formats

/** Created by smanciot on 19/04/2021.
  */
package object serialization {

  val stuartFormats: Formats = commonFormats ++ StuartPBSerializers.all
}
