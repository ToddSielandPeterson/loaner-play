package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.ProductDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.ProductMongo
import com.cognitivecreations.modelconverters.ProductConverter
import models.Product
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/13/15.
 */

class ProductCoordinator(implicit ec: ExecutionContext) extends ProductConverter with CoordinatorBase[Product] {

  val productDao = new ProductDao()

  def findByPrimary(uuid: UUID): Future[Option[Product]] = {
    for {
      optCat <- productDao.findByProductId(uuid.toString)
    } yield
      optCat.map(x => fromMongo(x))
  }

  def insert(product: Product): Future[LastError] = {
    findByPrimary(product.productId).flatMap {
      case Some(s) => failed(s"Category ${product.productId.toString} already exists")
      case None => productDao.insert(toMongo(product))
    }
  }

}
