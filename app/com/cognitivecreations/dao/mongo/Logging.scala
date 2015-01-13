package com.cognitivecreations.dao.mongo

import com.cognitivecreations.helpers.XmlUtil

import scala.util.Try
import scala.xml.NodeSeq

trait Logging {

  protected lazy val log = org.slf4j.LoggerFactory.getLogger(this.getClass)

  def trace(msg: => AnyRef, e: Throwable = null) {
    if (log.isTraceEnabled) log.trace(msg.toString, e)
  }

  def debug(msg: => AnyRef, e: Throwable = null) {
    if (log.isDebugEnabled) log.debug(msg.toString, e)
  }

  def info(msg: => AnyRef, e: Throwable = null) {
    if (log.isInfoEnabled) log.info(msg.toString, e)
  }

  def warn(msg: => AnyRef, e: Throwable = null) {
    if (log.isWarnEnabled) log.warn(msg.toString, e)
  }

  def error(msg: => AnyRef, e: Throwable = null) {
    if (log.isErrorEnabled) {
      msg match {
        case ex: Throwable => log.error("Unexpected Throwable", ex)
        case _ => log.error(msg.toString, e)
      }
    }
  }

  /**
   * Convenient stop watch method
   * @param startTimeNs start time in ns
   * @param msg the message should contain %s at the place where the time should be inserted.
   */
  def debug(startTimeNs: Long, msg: => String) {
    debug(msg format printDelta(startTimeNs))
  }

  /**
   * Convenient stop watch method
   * @param startTimeNs start time in ns
   * @param msg the message should contain %s at the place where the time should be inserted.
   */
  def info(startTimeNs: => Long, msg: => String) {
    info(msg format printDelta(startTimeNs))
  }

  /**
   * Prints the time delta from the given start time to now in seconds.
   * @param startTimeNanoSec the start time in nanoseconds (e.g. System.nanoTime())
   * @return the formatted delta in seconds.
   */
  private def printDelta(startTimeNanoSec: Long): String = {
    val delta = System.nanoTime() - startTimeNanoSec

    val nanoFraction = delta % 1000000
    val milliTotal = delta / 1000000
    val milliFraction = milliTotal % 1000
    val secTotal = milliTotal / 1000

    "%d s, %d ms, %d ns".format(secTotal, milliFraction, nanoFraction)
  }

  def printXml(xml: NodeSeq): String = {
    Try(new XmlUtil {}.prettyPrint(xml)).getOrElse("xml:did_not_print")
  }
}

object Logging {
  trait tstLogging { implicit val tstLogging: Logging }

  // NOTE - this shutdown hooks depends on the underlying logger facility being logback, should that ever change this also needs to be adjusted to account for that
  import ch.qos.logback.classic.LoggerContext
  def shutdown {
    val lc = org.slf4j.LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    lc.stop()
  }
}