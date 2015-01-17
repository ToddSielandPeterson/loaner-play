package com.cognitivecreations.dao.mongo.dao.mongomodel

import java.util.UUID

/**
 * Created by Todd Sieland-Peteson on 1/15/15.
 */
case class UserReviewsMongo(reviewer: UUID,
                        owner: UUID,
                        product: UUID,
                        rating: Int, // 0-5
                        bodyText: String)

object UserReviewsMongo {
  implicit lazy val bsonHandler_userReviewsMongo = {
    import com.cognitivecreations.helpers.BSONHandlers._
    import com.cognitivecreations.helpers.BSONHelpers._
    reactivemongo.bson.Macros.handler[UserReviewsMongo]
  }
}
