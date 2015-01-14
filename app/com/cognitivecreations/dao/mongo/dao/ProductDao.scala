package com.cognitivecreations.dao.mongo.dao

import com.cognitivecreations.dao.mongo.dao.mongomodel.{UserMongo, DatabaseMongoDataSource, ProductMongo}
import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.helpers.BSONHandlers
import models.Product
import reactivemongo.api.DB
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONString, BSONDocument, BSONObjectID}

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
  def findByProductId(id: String): Future[Option[ProductMongo]] = {
    findOne(BSONDocument("productId" -> BSONString(id)))
  }
}

