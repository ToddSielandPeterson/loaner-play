package com.cognitivecreations.modelconvertors

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.ProductMongo
import models.Product
import reactivemongo.bson.BSONObjectID

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Todd Sieland-Peteson on 1/11/15.
 */
object ProductConvertor {
  def asFutureProductDao(userInF: Future[ProductMongo])(implicit executionContext: ExecutionContext): Future[Product] =
    for (u <- userInF) yield asProduct(u)
  def asFutureProductDaoMongo(userInF: Future[Product])(implicit executionContext: ExecutionContext): Future[ProductMongo] =
    for (u <- userInF) yield asProductMongo(u)

  def asFutureOptionProductDao(userInF: Future[Option[ProductMongo]])(implicit executionContext: ExecutionContext): Future[Option[Product]] =
    for (u <- userInF) yield if (u.isDefined) Some(asProduct(u.get)) else None
  def asFutureOptionProductDaoMongo(userInF: Future[Option[Product]])(implicit executionContext: ExecutionContext): Future[Option[ProductMongo]] =
    for (u <- userInF) yield if (u.isDefined) Some(asProductMongo(u.get)) else None

  def asProduct(product: ProductMongo): Product = {
    Product(id = Some(product.id.toString),
      user = product.user,
      name = product.name,
      secondLine = product.secondLine,
      categoryId = product.categoryId,
      productType = product.productType,
      addedDateTime = product.addedDateTime,
      lastUpdate = product.lastUpdate,
      pictures = product.pictures,
      thumbnails = product.thumbnails,
      text = product.text)
  }

  def asProductMongo(product: Product): ProductMongo = {
    ProductMongo(id = UUID.fromString(product.id.get),
      user = product.user,
      name = product.name,
      secondLine = product.secondLine,
      categoryId = product.categoryId,
      productType = product.productType,
      addedDateTime = product.addedDateTime,
      lastUpdate = product.lastUpdate,
      pictures = product.pictures,
      thumbnails = product.thumbnails,
      text = product.text)
  }
}


