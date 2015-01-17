package com.cognitivecreations.dao.mongo.dao.mongomodel

import java.util.UUID

import org.joda.time.DateTime

/**
 * Created by tsieland on 10/14/14.
 */
case class PagesMongo(userPageId: UUID,
                      name: String,
                      header: String,
                      tags: List[String],
                      text: String,
                      active: Boolean,
                      created: Option[DateTime] = None,
                      updated: Option[DateTime] = None)

object PagesMongo {
  implicit lazy val bsonHandler_PagesMongo = {
    import com.cognitivecreations.helpers.BSONHandlers._
    import com.cognitivecreations.helpers.BSONHelpers._
    reactivemongo.bson.Macros.handler[PagesMongo]
  }
}