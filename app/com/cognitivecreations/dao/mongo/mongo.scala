package com.cognitivecreations.dao

import com.cognitivecreations.helpers.MonitoringConstants
import org.slf4j.MDC
import play.api.Logger

import scala.annotation.tailrec

package object mongo {

  val SESSION_ID = "session_id"

  val log = Logger("util")

  @tailrec
  def getRootCause(e: Throwable): Throwable = {
    val c = e.getCause()
    if (c == null || c == e) e
    else getRootCause(c)
  }

  def round(Rval: Float, Rpl: Int): Double = {
    round(Rval.toDouble, Rpl)
  }

  def round(Rval: Double, Rpl: Int): Double = {
    val p: Double = Math.pow(10, Rpl)
    Math.round(Rval * p) / p
  }

  /**
   * Prints the time delta from the given start time to now in seconds.
   * @param startTimeNanoSec the start time in nanoseconds (e.g. System.nanoTime())
   * @return the formatted delta in seconds.
   */
  def printDelta(startTimeNanoSec: Long): String = {
    val delta = System.nanoTime() - startTimeNanoSec

    val nanoFraction = delta % 1000000
    val milliTotal = delta / 1000000
    val milliFraction = milliTotal % 1000
    val secTotal = milliTotal / 1000

    "%d s, %d ms, %d ns".format(secTotal, milliFraction, nanoFraction)
  }

  // TODO - the product name and group id are here for monitoring, these two will eventually go away once monitoring is an instance passed with the execution context instead of a trait
  /**
   * Set the MDC session id, product name, and group id.
   */
  def setMDC(sessionId: String, productOpt: Option[String], groupIdOpt: Option[String]) {
    MDC.put(SESSION_ID, sessionId)
    productOpt.map(MDC.put(MonitoringConstants.MONITOR_PRODUCT, _))
    groupIdOpt.map(MDC.put(MonitoringConstants.MONITOR_GROUP, _))
  }

  /**
   * Get the current MDC session id.
   */
  def getMDCSessionId: String = MDC.get(SESSION_ID)
  def getMDCProduct: Option[String] = Option(MDC.get(MonitoringConstants.MONITOR_PRODUCT))
  def getMDCGroupId: Option[String] = Option(MDC.get(MonitoringConstants.MONITOR_GROUP))

  /**
   * Clear the MDC session id.
   */
  def unsetMDC {
    MDC.remove(SESSION_ID)
    MDC.remove(MonitoringConstants.MONITOR_PRODUCT)
    MDC.remove(MonitoringConstants.MONITOR_GROUP)
  }
}
