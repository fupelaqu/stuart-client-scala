package app.softnetwork.stuart.client.scala.serialization

import app.softnetwork.protobuf.ScalaPBSerializers
import ScalaPBSerializers.GeneratedEnumSerializer
import app.softnetwork.stuart.client.scala.model.{PackageType, TransportType, JobStatus, DeliveryStatus}

/**
  * Created by smanciot on 02/04/2021.
  */
object StuartPBSerializers {
  def all = Seq(
    GeneratedEnumSerializer(DeliveryStatus.enumCompanion),
    GeneratedEnumSerializer(JobStatus.enumCompanion),
    GeneratedEnumSerializer(TransportType.enumCompanion),
    GeneratedEnumSerializer(PackageType.enumCompanion)
  )
}
