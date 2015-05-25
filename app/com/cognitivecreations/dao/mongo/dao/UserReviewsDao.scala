package com.cognitivecreations.dao.mongo.dao

import java.util.UUID

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.{ImageMongo, UserReviewsMongo, DatabaseMongoDataSource, CategoryMongo}
import com.cognitivecreations.helpers.BSONHandlers
import org.joda.time.DateTime
import reactivemongo.bson.{BSONString, BSONDocument}
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/15/15.
 */
trait UserReviewsDaoTrait extends BaseMongoDao[UserReviewsMongo] with BSONHandlers {
  import reactivemongo.api._
  val collection =   DatabaseMongoDataSource.userReviewsSource
  implicit val bsonHandler = UserReviewsMongo.bsonHandler_userReviewsMongo
}

class UserReviewsDao(implicit val executionContext: ExecutionContext) extends UserReviewsDaoTrait {
  def byReviewerKey(value: String): BSONDocument = {
    BSONDocument("reviewer" -> BSONString(value))
  }
  def byOwnerKey(value: String): BSONDocument = {
    BSONDocument("owner" -> BSONString(value))
  }
  def byProductKey(value: String): BSONDocument = {
    BSONDocument("product" -> BSONString(value))
  }
  def byMainKey(id: String): BSONDocument = {
    BSONDocument("id" -> BSONString(id))
  }
  def byMainKey(id: UUID): BSONDocument = {
    BSONDocument("id" -> BSONString(id.toString))
  }
  def update(userReview: UserReviewsMongo): Future[LastError] = {
    val upsertProduct = userReview.copy(lastUpdate = DateTime.now())
    update(byMainKey(userReview.id.toString), update = upsertProduct, upsert = true, multi = false)
  }

}

