package com.cognitivecreations.dao.mongo.dao

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.{UserReviewsMongo, DatabaseMongoDataSource, CategoryMongo}
import com.cognitivecreations.helpers.BSONHandlers
import reactivemongo.bson.{BSONString, BSONDocument}

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
}

