package com.cognitivecreations.dao.mongo.dao.mongomodel

import java.util.UUID

import org.joda.time.DateTime

/**
 * Created by Todd Sieland-Peteson on 1/15/15.
 */

case class SessionMongo (sessionId: UUID,
                         userId: Option[UUID] = None,
                         orderNumber: Option[UUID] = None,
                         loggedInRecently: Option[Boolean] = None,
                         activeDate: DateTime = new DateTime()) {
  def loggedIn:Boolean = userId.isDefined && loggedInRecently.getOrElse(false)
  def unknownUser:Boolean = !loggedIn
}

object SessionMongo {
  implicit lazy val bsonHandler_SessionMongo = {
    import com.cognitivecreations.helpers.BSONHandlers._
    import com.cognitivecreations.helpers.BSONHelpers._
    reactivemongo.bson.Macros.handler[SessionMongo]
  }
}
