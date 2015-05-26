package com.cognitivecreations.dao.mongo.dao

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.{DatabaseMongoDataSource, ProductMongo}
import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.helpers.BSONHandlers
import org.joda.time.DateTime
import reactivemongo.bson.{BSONString, BSONDocument}
import reactivemongo.core.commands.{GetLastError, LastError}

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
  def byMainKey(id: String): BSONDocument = { byKey("id", id)  }

  def findByProductId(id: UUID): Future[Option[ProductMongo]] = {
    findOne(byMainKey(id))
  }

  def deleteByProductId(id: UUID): Future[LastError] = {
    delete(byMainKey(id), true)
  }

  def findByCategory(id: UUID): Future[List[ProductMongo]] = {
    find(byKey("categoryId",id.toString))
  }

  def findByUser(id: UUID): Future[List[ProductMongo]] = {
    find(byKey("user", id.toString))
  }

  def update(product: ProductMongo): Future[LastError] = {
    val upsertProduct = product.copy(lastUpdate = Some(new DateTime))
    update(byMainKey(product.id), update = upsertProduct, upsert = true, multi = false)
  }
}

