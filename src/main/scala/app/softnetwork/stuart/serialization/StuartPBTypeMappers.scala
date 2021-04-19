package app.softnetwork.stuart.serialization

import app.softnetwork.stuart.message._
import org.json4s.{Formats, jackson}

import scala.util.{Failure, Success, Try}
import scalapb.TypeMapper

/**
  * Created by smanciot on 02/04/2021.
  */
object StuartPBTypeMappers {

  implicit val arrayOfStringTypeMapper: TypeMapper[ArrayOfString, Seq[String]] =
    new TypeMapper[ArrayOfString, Seq[String]]{
      override def toBase(custom: Seq[String]): ArrayOfString = ArrayOfString(custom)
      override def toCustom(base: ArrayOfString): Seq[String] = base.values
    }

  implicit val toGenericEvent: TypeMapper[StuartEvent, StuartGenericEvent] =
    new TypeMapper[StuartEvent, StuartGenericEvent]{
      implicit def formats: Formats = stuartFormats
      implicit def serialization = jackson.Serialization
      override def toBase(custom: StuartGenericEvent): StuartEvent = {
        val stuartEvent = StuartEvent.defaultInstance.copy(
          event = custom.event,
          `type` = custom.`type`
        )
        custom.data match {
          case None => stuartEvent
          case Some(data) =>
            val input = serialization.write(data)
            custom match {
              case StuartGenericEvent(Some("job"), t@Some(atype), _) =>
                atype match {
                  case "create" =>
                    Try(serialization.read[JobEvent](input)) match {
                      case Success(job) => stuartEvent.withData(job)
                      case Failure(f) => stuartEvent
                    }
                  case "update" =>
                    Try(serialization.read[JobEvent](input)) match {
                      case Success(job) => stuartEvent.withData(job)
                      case Failure(f) => stuartEvent
                    }
                  case _ => stuartEvent
                }
              case StuartGenericEvent(Some("delivery"), t@Some(atype), _) =>
                atype match {
                  case "create" =>
                    Try(serialization.read[CurrentDeliveryEvent](input)) match {
                      case Success(delivery) => stuartEvent.withData(delivery)
                      case Failure(f) => stuartEvent
                    }
                  case "update" =>
                    Try(serialization.read[CurrentDeliveryEvent](input)) match {
                      case Success(delivery) => stuartEvent.withData(delivery)
                      case Failure(f) => stuartEvent
                    }
                  case _ => stuartEvent
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


      override def toCustom(base: StuartEvent): StuartGenericEvent = base.data match {
        case StuartEventData.Empty =>
          StuartGenericEvent(base.event, base.`type`, None)
        case data =>
          StuartGenericEvent(
            base.event,
            base.`type`,
            Option(
              serialization.read[Map[String, AnyRef]](serialization.write(data))
            )
          )
      }
    }
}
