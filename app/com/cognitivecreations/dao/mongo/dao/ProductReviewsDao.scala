package com.cognitivecreations.dao.mongo.dao

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.{ProductReviewsMongo, UserReviewsMongo, DatabaseMongoDataSource}
import com.cognitivecreations.helpers.BSONHandlers
import reactivemongo.bson.{BSONString, BSONDocument}

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/15/15.
 */
trait ProductReviewsDaoTrait extends BaseMongoDao[ProductReviewsMongo] with BSONHandlers {
  import reactivemongo.api._
  val collection =   DatabaseMongoDataSource.productReviewsSource
  implicit val bsonHandler = ProductReviewsMongo.bsonHandler_ProductReviews
}

class ReviewsDao(implicit val executionContext: ExecutionContext) extends ProductReviewsDaoTrait {
  def findReviewsFromUser(id: String): Future[List[ProductReviewsMongo]] = {
    find(BSONDocument("reviewer" -> BSONString(id)))
  }
  def findReviewsAboutUser(id: String): Future[List[ProductReviewsMongo]] = {
    find(BSONDocument("owner" -> BSONString(id)))
  }
  def findReviewsAboutProduct(id: String): Future[List[ProductReviewsMongo]] = {
    find(BSONDocument("product" -> BSONString(id)))
  }
}


