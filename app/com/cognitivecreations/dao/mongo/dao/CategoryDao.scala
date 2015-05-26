package com.cognitivecreations.dao.mongo.dao

import java.util.UUID

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.{CategoryMongo, DatabaseMongoDataSource}
import com.cognitivecreations.helpers.BSONHandlers
import reactivemongo.bson.{BSONString, BSONDocument}
import reactivemongo.core.commands.LastError

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

  def byCategoryId(uuid: String): BSONDocument = byKey("categoryId", uuid)

  def findByCategoryId(id: String): Future[Option[CategoryMongo]] = {
    findOne(byCategoryId(id))
  }

  def findByCategoryUniqueName(id: String): Future[Option[CategoryMongo]] = {
    findOne(byKey("uniqueName", id))
  }

  def delete(id: String): Future[LastError] = {
    delete(byCategoryId(id), first = true)
  }

  def update(categoryMongo: CategoryMongo): Future[LastError] = {
    update(byCategoryId(categoryMongo.categoryId.toString), categoryMongo)
  }
}

