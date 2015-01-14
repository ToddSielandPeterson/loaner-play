package com.cognitivecreations.modelconverters

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.CategoryMongo
import models.Category

/**
 * Created by Todd Sieland-Peteson on 1/13/15.
 */
class CategoryConverter extends ModelConverterBase[Category, CategoryMongo] {
  def fromMongo(category: CategoryMongo): Category = {
    Category(categoryId = UUID.fromString(category.categoryId), name = category.categoryName, uniqueName = category.uniqueName,
      ordering = category.ordering, parentId = category.parentId.map(stringToUUID))
  }

  def toMongo(category: Category): CategoryMongo = {
    CategoryMongo(categoryId = category.categoryId.toString, categoryName = category.name, uniqueName = category.uniqueName,
      ordering = category.ordering, parentId = category.parentId.map(uuidToString))
  }

}
