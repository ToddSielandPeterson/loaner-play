package com.cognitivecreations.dao.mongo.dao

import java.util.UUID

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

class ProductReviewsDao(implicit val executionContext: ExecutionContext) extends ProductReviewsDaoTrait {
  
  def byReviewerKey(id:String) : BSONDocument = byKey("reviewerId", id)
  def byProductKey(id:String) : BSONDocument = byKey("productId", id)
  def byOrderKey(id:String) : BSONDocument = byKey("orderId", id)
  def byOwnerKey(id:String) : BSONDocument = byKey("ownerId", id)
  
  def findReviewsFromReviewer(id: UUID): Future[List[ProductReviewsMongo]] = { find(byReviewerKey(id)) }
  def findReviewsAboutOrder(id: UUID): Future[List[ProductReviewsMongo]] = { find(byOrderKey(id))  }
  def findReviewsAboutProduct(id: UUID): Future[List[ProductReviewsMongo]] = { find(byProductKey(id))  }
  def findReviewsAboutOnwner(id: UUID): Future[List[ProductReviewsMongo]] = { find(byOwnerKey(id))  }
}


