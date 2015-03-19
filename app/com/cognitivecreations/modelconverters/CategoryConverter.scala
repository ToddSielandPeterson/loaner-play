package com.cognitivecreations.modelconverters

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.CategoryMongo
import models.Category
import com.cognitivecreations.helpers.Pimp_UUID._

/**
 * Created by Todd Sieland-Peteson on 1/13/15.
 */
class CategoryConverter extends ModelConverterBase[Category, CategoryMongo] {
  def fromMongo(category: CategoryMongo): Category = {
    Category(categoryId = Some(category.categoryId), name = category.categoryName, uniqueName = category.uniqueName,
      ordering = category.ordering, parentId = category.parentId)
  }

  def toMongo(category: Category): CategoryMongo = {
    CategoryMongo(categoryId = category.categoryId.get.toString,
      categoryName = category.name,
      uniqueName = category.uniqueName,
      ordering = category.ordering,
      parentId = category.parentId.map(uuidToString))
  }

}
