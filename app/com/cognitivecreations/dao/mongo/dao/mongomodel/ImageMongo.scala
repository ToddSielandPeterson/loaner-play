package com.cognitivecreations.dao.mongo.dao.mongomodel

import java.util.UUID

import org.joda.time.DateTime

/**
 * Created by Todd Sieland-Peteson on 5/4/15.
 */

case class ImageMongo(imageId: Option[UUID],
                      userId: Option[UUID],
                      image: String,  // full url
                      thumbnail: Option[String],
                      adminOk: Boolean, // if admin has ok'd
                      activeImage:Boolean, // if the image can be shown
                      lastUpdate: Option[DateTime])

object ImageMongo {
  implicit lazy val bsonHandler_ImageMongo = {
    import com.cognitivecreations.helpers.BSONHandlers._
    import com.cognitivecreations.helpers.BSONHelpers._
    reactivemongo.bson.Macros.handler[ImageMongo]
  }
}