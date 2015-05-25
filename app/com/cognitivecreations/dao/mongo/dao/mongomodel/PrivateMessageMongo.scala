package com.cognitivecreations.dao.mongo.dao.mongomodel

import java.util.UUID

import org.joda.time.DateTime

/**
 * Created by Todd Sieland-Peteson on 5/25/15.
 */
case class PrivateMessageMongo(id: UUID,
                       fromUser: UUID,
                       toUser: UUID,
                       subject: String,
                       body: String,
                       attachments: List[String] = List(),
                       newMessage: Boolean = true,
                       read: Boolean = false,
                       replyId: Option[UUID] = None, // uuid of Private Message back
                       created: Option[DateTime] = None,
                       lastUpdated: Option[DateTime] = None,
                       opened: Option[DateTime] = None)

object PrivateMessageMongo {
  implicit lazy val bsonHandler_PrivateMessageMongo = {
    import com.cognitivecreations.helpers.BSONHandlers._
    import com.cognitivecreations.helpers.BSONHelpers._
    reactivemongo.bson.Macros.handler[PrivateMessageMongo]
  }
}