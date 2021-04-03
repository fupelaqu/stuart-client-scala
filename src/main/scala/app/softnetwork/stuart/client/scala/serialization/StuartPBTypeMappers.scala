package app.softnetwork.stuart.client.scala.serialization

import app.softnetwork.stuart.client.scala.message.ArrayOfString

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
}
