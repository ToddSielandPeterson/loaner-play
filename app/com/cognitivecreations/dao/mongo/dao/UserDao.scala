package com.cognitivecreations.dao.mongo.dao

import java.util.UUID

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.{UserMongo, DatabaseMongoDataSource}
import com.cognitivecreations.helpers.BSONHandlers
import reactivemongo.bson.{BSONString, BSONDocument}

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/11/15.
 */

trait UserMongoDaoTrait extends BaseMongoDao[UserMongo] with BSONHandlers {
  import reactivemongo.api._
  val collection =   DatabaseMongoDataSource.userDataSource
  implicit val bsonHandler = UserMongo.bsonHandler_UserMongoProperty
}

class UserMongoDao(implicit val executionContext: ExecutionContext) extends UserMongoDaoTrait {
  import UserMongo._

  def findByUserId(id: UUID): Future[Option[UserMongo]] = {
    findOne(BSONDocument("userId" -> BSONString(id.toString)))
    //collection.find(BSONDocument("userId" -> BSONString(id))).one[UserMongo]
  }
}


