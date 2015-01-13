package com.cognitivecreations.dao.mongo.dao

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.{PagesMongo, DatabaseMongoDataSource, ProductMongo, UserMongo}
import com.cognitivecreations.helpers.BSONHandlers
import reactivemongo.bson.{BSONString, BSONDocument}

import scala.concurrent.{Future, ExecutionContext}

trait PagesDaoTrait extends BaseMongoDao[PagesMongo] with BSONHandlers {
  import reactivemongo.api._
  val collection =   DatabaseMongoDataSource.pagesSource
  implicit val bsonHandler = PagesMongo.bsonHandler_PagesMongo
}


class PagesDao(implicit val executionContext: ExecutionContext) extends PagesDaoTrait {
  def findByPageId(id: String): Future[Option[PagesMongo]] = {
    findOne(BSONDocument("userId" -> BSONString(id)))
    //collection.find(BSONDocument("userId" -> BSONString(id))).one[UserMongo]
  }
}

