package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.CategoryDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.CategoryMongo
import com.cognitivecreations.modelconverters.CategoryConverter
import models.Category
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/13/15.
 */
class CategoryCoordinator(implicit ec: ExecutionContext) extends CategoryConverter with CoordinatorBase[Category] {
  val categoryDao = new CategoryDao()

  def findByPrimary(uuid: UUID): Future[Option[Category]] = {
    for {
      optCat <- categoryDao.findByCategoryId(uuid.toString)
    } yield
      optCat.map(x => fromMongo(x))
  }

  def insert(category: Category): Future[LastError] = {
    findByPrimary(category.categoryId).flatMap {
      case Some(s) => failed(s"Category ${category.categoryId} already exists")
      case None => categoryDao.insert(toMongo(category))
    }
  }

}
