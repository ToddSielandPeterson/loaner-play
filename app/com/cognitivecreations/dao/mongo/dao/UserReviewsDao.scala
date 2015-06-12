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
  def byReviewerKey(value: String): BSONDocument = { byKey("reviewer", value)  }
  def byOwnerKey(value: String): BSONDocument = { byKey("owner", value)  }
  def byProductKey(value: String): BSONDocument = { byKey("product", value)  }
  def byMainKey(id: String): BSONDocument = { byKey("id", id)  }

  def findByReviewer(uuid: UUID): Future[List[UserReviewsMongo]] = {
    find(byReviewerKey(uuid))
  }

  def findByOwner(uuid: UUID): Future[List[UserReviewsMongo]] = {
    find(byOwnerKey(uuid))
  }

  def findByProduct(uuid: UUID): Future[List[UserReviewsMongo]] = {
    find(byProductKey(uuid))
  }

  def delete(uuid: UUID): Future[LastError] = {
    delete(byMainKey(uuid), true)
  }

  def update(userReview: UserReviewsMongo): Future[LastError] = {
    val upsertProduct = userReview.copy(lastUpdate = DateTime.now())
    update(byMainKey(userReview.id.toString), update = upsertProduct, upsert = true, multi = false)
  }

}

