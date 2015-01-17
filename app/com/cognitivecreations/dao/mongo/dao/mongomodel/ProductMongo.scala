package com.cognitivecreations.dao.mongo.dao.mongomodel

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json.Json
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID}

/**
 * Created by tsieland on 10/14/14.
 */
case class ProductMongo(id: UUID, // unique generated id (UUID)
                        user: String, // link to user id

                        name: String,
                        secondLine: Option[String] = None,
                        categoryId: Option[UUID] = None, // link to unique category id
                        productType: Option[String] = None,

                        addedDateTime: Option[DateTime] = None,
                        lastUpdate: Option[DateTime] = None,

                        pictures: List[String], // list of pictures
                        thumbnails: List[String], // list of pictures
                        text: String)

object ProductMongo {
  implicit lazy val bsonHandler_ProductMongo = {
    import com.cognitivecreations.helpers.BSONHandlers._
    import com.cognitivecreations.helpers.BSONHelpers._

    reactivemongo.bson.Macros.handler[ProductMongo]
  }
}