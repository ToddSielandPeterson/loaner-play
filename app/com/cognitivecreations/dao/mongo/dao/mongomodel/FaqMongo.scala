package com.cognitivecreations.dao.mongo.dao.mongomodel

import java.util.UUID

import org.joda.time.DateTime

/**
 * Created by Todd Sieland-Peteson on 5/25/15.
 */
case class FaqMongo (faqId:UUID, orderingIndex: Int, title:String, richText: String,
                     author: Option[String], tags: List[String], vote: Option[Int],
                     create: DateTime = DateTime.now(), lastUpdate: DateTime = DateTime.now(),
                     showUntil: DateTime = DateTime.now().plusYears(2), showFrom: DateTime = DateTime.now())

object FaqMongo {
  implicit lazy val bsonHandler_FaqMongo = {
    import com.cognitivecreations.helpers.BSONHandlers._
    import com.cognitivecreations.helpers.BSONHelpers._

    reactivemongo.bson.Macros.handler[FaqMongo]
  }
}
