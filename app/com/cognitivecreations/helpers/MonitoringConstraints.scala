package com.cognitivecreations.helpers

object MonitoringConstants {
  /**
   * Available things to monitor, if you don't see what you need then add it and notify techops of the new value.
   */
  sealed trait Monitorable

  sealed trait ExternalMonitorable extends Monitorable
  case object PlayAppExternal extends ExternalMonitorable

  sealed trait InternalMonitorable extends Monitorable
  case object PlayApp extends InternalMonitorable
  case object Mysql extends InternalMonitorable
  case object Mongo extends InternalMonitorable
  case object DataManager extends InternalMonitorable
  case object Admin extends InternalMonitorable

  /**
   * Top level type for monitorable action for serializer consistency in monitoring recorders.
   */
  sealed trait MonitorableAction {
    def name: String
    def description: Option[String]
  }

  /**
   * Available monitorable actions that require business validation, extend this when the action could fail for exceptional reasons
   * or anticipated business reasons and the user must provide a means of determining the outcome.
   *
   * Examples: interaction with external providers
   */
  sealed trait MonitorableBusinessAction extends MonitorableAction
  case class Search(description: Option[String] = None) extends MonitorableBusinessAction {
    def name = "search"
  }
  case class ProviderData(description: Option[String] = None) extends MonitorableBusinessAction {
    def name = "provider_data"
  }
  case class BookSearch(description: Option[String] = None) extends MonitorableBusinessAction {
    def name = "book_search"
  }
  case class BookStart(description: Option[String] = None) extends MonitorableBusinessAction {
    def name = "book_start"
  }
  case class BookComplete(description: Option[String] = None) extends MonitorableBusinessAction {
    def name = "book_complete"
  }
  case class Cancel(description: Option[String] = None) extends MonitorableBusinessAction {
    def name = "cancel"
  }
  case class Modify(description: Option[String] = None) extends MonitorableBusinessAction {
    def name = "modify"
  }

  /**
   * Available monitorable actions that are only expected to fail in exceptional conditions, extend this when the
   * operation really just needs to be timed and checked for unanticipated failure.
   *
   * Examples: top level play requests, data store queries/persistence, serialization, computations
   */
  sealed trait MonitorableExceptionAction extends MonitorableAction
  case class PlayRequest(description: Option[String] = None) extends MonitorableExceptionAction {
    def name = "play_request"
  }
  case class Query(description: Option[String] = None) extends MonitorableExceptionAction {
    def name = "query"
  }
  case class Persist(description: Option[String] = None) extends MonitorableExceptionAction {
    def name = "persist"
  }
  case class Serialization(description: Option[String] = None) extends MonitorableExceptionAction {
    def name = "serialization"
  }
  case class Computation(description: Option[String] = None) extends MonitorableExceptionAction {
    def name = "computation"
  }
  case class Report(description: Option[String] = None) extends MonitorableExceptionAction {
    def name = "report"
  }

  /**
   * Operation statuses, if you need a new one add it and notify techops of the new value.
   */
  sealed trait MonitorableStatus {
    def failureReason: Option[String] = None
  }
  case object MonitorableSuccess extends MonitorableStatus {
    override def toString = "SUCCESS"
  }
  case class MonitorableBusinessFailure(reason: String) extends MonitorableStatus {
    override def toString = "FAILURE"
    override def failureReason = Option(reason)
  }
  case class MonitorableExceptionFailure(t: Throwable) extends MonitorableStatus {
    override def toString = "FAILURE"
    override def failureReason = Option(t.getMessage)
  }

  /**
   * Temporary keys for propagating monitoring data via the MDC, eventually this will become part of propagating a whole monitoring instance via the execution context.
   */
  val MONITOR_PRODUCT = "monitor_product_name"
  val MONITOR_GROUP = "monitor_group_id"
}
