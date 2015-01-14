package com.cognitivecreations.dao.mongo.dao.mongomodel

import java.util.UUID

/**
 * Created by Todd Sieland-Peteson on 1/11/15.
 */

case class CategoryMongo (categoryId: String, categoryName: String, uniqueName: String, ordering: Int, parentId: Option[String])

object CategoryMongo {
  implicit lazy val bsonHandler_CategoryMongo = {
    import com.cognitivecreations.helpers.BSONHelpers._
    reactivemongo.bson.Macros.handler[CategoryMongo]
  }
}
