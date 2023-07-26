package app.softnetwork.stuart.server

import app.softnetwork.stuart.message.{DeliveryEvent, DriverEvent, JobEvent}

trait StuartCallBacks {

  /** @param job
    *   - the created job event
    */
  def jobCreated(job: JobEvent): Unit = ()

  /** @param job
    *   - the updated job event
    */
  def jobUpdated(job: JobEvent): Unit = ()

  /** @param delivery
    *   - the created delivery event
    */
  def deliveryCreated(delivery: DeliveryEvent): Unit = ()

  /** @param delivery
    *   - the updated delivery event
    */
  def deliveryUpdated(delivery: DeliveryEvent): Unit = ()

  /** @param driver
    *   - the updated driver event
    */
  def driverUpdated(driver: DriverEvent): Unit = ()

}
