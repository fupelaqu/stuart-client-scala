package app.softnetwork.stuart.serialization

import app.softnetwork.protobuf.ScalaPBSerializers.GeneratedEnumSerializer
import app.softnetwork.stuart.message.{Canceller, JobListingOrder}
import app.softnetwork.stuart.model.{DeliveryStatus, JobStatus, PackageType, TransportType}

/** Created by smanciot on 02/04/2021.
  */
object StuartPBSerializers {
  def all = Seq(
    GeneratedEnumSerializer(DeliveryStatus.enumCompanion),
    GeneratedEnumSerializer(JobStatus.enumCompanion),
    GeneratedEnumSerializer(TransportType.enumCompanion),
    GeneratedEnumSerializer(PackageType.enumCompanion),
    GeneratedEnumSerializer(JobListingOrder.enumCompanion),
    GeneratedEnumSerializer(Canceller.enumCompanion)
  )
}
