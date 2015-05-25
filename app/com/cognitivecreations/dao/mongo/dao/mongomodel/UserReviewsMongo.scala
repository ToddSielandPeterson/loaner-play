package com.cognitivecreations.dao.mongo.dao.mongomodel

import java.util.UUID

import org.joda.time.DateTime

/**
 * Created by Todd Sieland-Peteson on 1/15/15.
 */
case class UserReviewsMongo(id: UUID,
                            reviewer: UUID,
                            owner: UUID,
                            product: Option[UUID],

                            lastUpdate: DateTime = DateTime.now(),
                            created: DateTime = DateTime.now(),
                            rating: Int = 0, // 0-5
                            title: String = "",
                            bodyText: String = "",
                            flagged: Option[Boolean] = None,
                            flagCount: Option[Int] = None,
                            weight: Int = 0)

object UserReviewsMongo {
  implicit lazy val bsonHandler_userReviewsMongo = {
    import com.cognitivecreations.helpers.BSONHandlers._
    import com.cognitivecreations.helpers.BSONHelpers._
    reactivemongo.bson.Macros.handler[UserReviewsMongo]
  }
}
