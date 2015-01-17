package com.cognitivecreations.dao.mongo.dao.mongomodel

import java.util.UUID

/**
 * Created by Todd Sieland-Peteson on 1/15/15.
 */
case class ProductReviewsMongo(ownerId: UUID, userId: UUID, productId: UUID, orderId: UUID, rating:Int, bodyText: String)

object ProductReviewsMongo {
  implicit lazy val bsonHandler_ProductReviews = {
    import com.cognitivecreations.helpers.BSONHandlers._
    import com.cognitivecreations.helpers.BSONHelpers._
    reactivemongo.bson.Macros.handler[ProductReviewsMongo]
  }
}
