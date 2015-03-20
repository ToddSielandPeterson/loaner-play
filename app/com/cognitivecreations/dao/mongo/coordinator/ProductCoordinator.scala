package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.ProductDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.ProductMongo
import com.cognitivecreations.dao.mongo.exceptions.{ProductDoesNotExists, NoUserBoundToSessionException}
import com.cognitivecreations.modelconverters.ProductConverter
import models.{User, Product}
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/13/15.
 */

class ProductCoordinator(implicit ec: ExecutionContext) extends ProductConverter with CoordinatorBase[Product] {

  val productDao = new ProductDao()

  def findByPrimary(uuid: UUID): Future[Option[Product]] = {
    for {
      optCat <- productDao.findByProductId(uuid)
    } yield
      optCat.map(x => fromMongo(x))
  }

  def findByCategory(uuid: UUID): Future[List[Product]] = {
    for {
      productList <- productDao.findByCategory(uuid)
    } yield
      productList.map(product => fromMongo(product))
  }

  def insert(product: Product): Future[LastError] = {
    productDao.insert(toMongo(product.copy(productId = Some(UUID.randomUUID()))))
  }

  def update(product: Product): Future[LastError] = {
    findByPrimary(product.productId).flatMap {
      case None => failed(s"Product ${product.productId.toString} does not exist")
      case Some(s) => productDao.update(toMongo(product))
    }
  }

  def findByOwner(user: User): Future[List[Product]] = {
    for {
      productList <- productDao.findByUser(user.userId.get)
    } yield
      productList.map(product => fromMongo(product))
  }

  def findByPrimaryAndUser(productId: UUID, user: Option[User]): Future[Option[Product]] = {
    if (user.isDefined) {
      findByPrimaryAndUser(productId, user.get.userId.get)
    } else {
      Future.failed(new NoUserBoundToSessionException())
    }
  }

  def findByPrimaryAndUser(productId: UUID, uuid: UUID): Future[Option[Product]] = {
    for {
      product <- productDao.findByProductId(productId)
    } yield {
      val x = product.map(product => fromMongo(product))
      x
    }
  }

  def delete(productId: UUID, user: Option[User]): Future[LastError] = {
    if (user.isDefined) {
      delete(productId, user.get.userId.get)
    } else {
      Future.failed(new NoUserBoundToSessionException())
    }
  }

  def delete(productId: UUID, uuid: UUID): Future[LastError] = {
    productDao.findByProductId(productId).flatMap(product => {
      if (product.isDefined && uuid == product.get.user)
        productDao.deleteByProductId(product.get.id)
      else
        Future.failed(new ProductDoesNotExists)
    })
  }
}
