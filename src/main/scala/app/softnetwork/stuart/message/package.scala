package app.softnetwork.stuart

import app.softnetwork.stuart.serialization._
import org.json4s.{jackson, Formats}

import scala.language.implicitConversions

import scala.util.{Failure, Success, Try}

/**
  * Created by smanciot on 19/04/2021.
  */
package object message {

  case class StuartGenericEvent(event: Option[String], `type`: Option[String], data: Option[Map[String, AnyRef]])

  object StuartGenericEvent{

    implicit def formats: Formats = stuartFormats

    implicit def serialization = jackson.Serialization

    implicit def toStuartEvent(custom: StuartGenericEvent): StuartEvent = {
      val stuartEvent = StuartEvent.defaultInstance.copy(
        event = custom.event,
        `type` = custom.`type`
      )
      custom.data match {
        case None => stuartEvent
        case Some(data) =>
          val input = serialization.write(data)
          custom match {
            case StuartGenericEvent(Some("job"), _, _) =>
              Try(serialization.read[JobEvent](input)) match {
                case Success(job) => stuartEvent.withData(job)
                case Failure(f) => stuartEvent
              }
            case StuartGenericEvent(Some("delivery"), _, _) =>
              Try(serialization.read[CurrentDeliveryEvent](input)) match {
                case Success(delivery) => stuartEvent.withData(delivery)
                case Failure(f) => stuartEvent
              }
            case StuartGenericEvent(Some("driver"), t@Some(atype), _) =>
              atype  match {
                case "update" =>
                  Try(serialization.read[DriverEvent](input)) match {
                    case Success(driver) => stuartEvent.withData(driver)
                    case Failure(f) => stuartEvent
                  }
                case _ => stuartEvent
              }
            case _ => stuartEvent
          }
      }
    }
  }
}
