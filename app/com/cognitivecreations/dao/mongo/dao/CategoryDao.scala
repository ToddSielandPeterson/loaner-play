package com.cognitivecreations.dao.mongo.dao

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.{CategoryMongo, DatabaseMongoDataSource}
import com.cognitivecreations.helpers.BSONHandlers
import reactivemongo.bson.{BSONString, BSONDocument}

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/11/15.
 */
trait CategoryDaoTrait extends BaseMongoDao[CategoryMongo] with BSONHandlers {
  import reactivemongo.api._
  val collection =   DatabaseMongoDataSource.categorySource
  implicit val bsonHandler = CategoryMongo.bsonHandler_CategoryMongo
}

class CategoryDao(implicit val executionContext: ExecutionContext) extends CategoryDaoTrait {
  def findByCategoryId(id: String): Future[Option[CategoryMongo]] = {
    findOne(BSONDocument("categoryId" -> BSONString(id)))
    //collection.find(BSONDocument("userId" -> BSONString(id))).one[UserMongo]
  }

  def findByCategoryUniqueName(id: String): Future[Option[CategoryMongo]] = {
    findOne(BSONDocument("uniqueName" -> BSONString(id)))
    //collection.find(BSONDocument("userId" -> BSONString(id))).one[UserMongo]
  }
}

