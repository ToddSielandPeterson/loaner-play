package com.cognitivecreations.modelconverters

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.ProductMongo
import models.Product

/**
 * Created by Todd Sieland-Peteson on 1/11/15.
 */

class ProductConverter extends ModelConverterBase[Product, ProductMongo] {
  def fromMongo(product: ProductMongo): Product = {
    Product(productId = Some(product.id),
      user = Some(product.user),
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

  def toMongo(product: Product): ProductMongo = {
    ProductMongo(id = product.productId.get,
      user = product.user.getOrElse(UUID.randomUUID()),
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


