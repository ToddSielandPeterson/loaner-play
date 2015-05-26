package com.cognitivecreations.dao.mongo.dao

import java.util.UUID

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.{ProductMongo, ImageMongo, DatabaseMongoDataSource, PagesMongo}
import com.cognitivecreations.helpers.BSONHandlers
import models.Image
import org.joda.time.DateTime
import reactivemongo.bson.{BSONString, BSONDocument}
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 5/4/15.
 */

trait ImageDaoTrait extends BaseMongoDao[ImageMongo] with BSONHandlers {
  import reactivemongo.api._
  val collection =   DatabaseMongoDataSource.image
  implicit val bsonHandler = ImageMongo.bsonHandler_ImageMongo
}


class ImageDao(implicit val executionContext: ExecutionContext) extends ImageDaoTrait {

  def byMainKey(id: String): BSONDocument = { byKey("imageId", id) }
  def byUserKey(id: String): BSONDocument = { byKey("userId", id) }

  def findByUrl(url: String): Future[Option[ImageMongo]] = {
    findOne(BSONDocument("image" -> BSONString(url)))
  }

  def findByUserId(id: UUID): Future[List[ImageMongo]] = { find(byUserKey(id)) }
  def findByImageId(id: UUID): Future[List[ImageMongo]] = { find(byMainKey(id)) }

  def update(image: ImageMongo): Future[LastError] = {
    val upsertProduct = image.copy(lastUpdate = Some(new DateTime))
    update(byMainKey(image.imageId.toString), update = upsertProduct, upsert = true, multi = false)
  }
}

