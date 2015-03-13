package com.cognitivecreations.dao.mongo.dao

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.{DatabaseMongoDataSource, ProductMongo}
import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.helpers.BSONHandlers
import reactivemongo.bson.{BSONString, BSONDocument}
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by tsieland on 10/21/14.
 */

trait ProductDaoTrait extends BaseMongoDao[ProductMongo] with BSONHandlers {
  import reactivemongo.api._
  val collection =   DatabaseMongoDataSource.productsDataSource
  implicit val bsonHandler = ProductMongo.bsonHandler_ProductMongo
}

class ProductDao(implicit val executionContext: ExecutionContext) extends ProductDaoTrait {

  def findByProductId(id: UUID): Future[Option[ProductMongo]] = {
    findOne(BSONDocument("id" -> BSONString(id.toString)))
  }

  def deleteByProductId(id: UUID): Future[LastError] = {
    delete(BSONDocument("id" -> BSONString(id.toString)), true)
  }

  def findByCategory(id: UUID): Future[List[ProductMongo]] = {
    find(BSONDocument("categoryId" -> BSONString(id.toString)))
  }

  def findByUser(id: UUID): Future[List[ProductMongo]] = {
    find(BSONDocument("user" -> BSONString(id.toString)))
  }
}

