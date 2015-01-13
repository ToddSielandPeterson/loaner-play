package com.cognitivecreations.dao.mongo.coordinator

import com.cognitivecreations.dao.mongo.dao.{CategoryDao, UserMongoDao}
import com.cognitivecreations.modelconvertors.UserConvertor._
import models.{Category, User}
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/13/15.
 */
class CategoryCoordinator(implicit ec: ExecutionContext) {

  def insert(category: Category): Future[LastError] = {
    try {
      val categoryDao = new CategoryDao()
      val y: Future[LastError] = categoryDao.findByCateoryId(category.userId.get).flatMap(optUserMongo =>
        if (optUserMongo.isDefined)
          Future.failed(new LastError(ok=false, code=None, err=Some("all"), errMsg = Some("already exists"), originalDocument = None, updated = 0, updatedExisting = false))
        else
          categoryDao.insert(asUserMongo(category))
      )
      y
    } catch {
      case ex: Exception =>
        println(s"Exception Happened ${ex.getMessage}" )
        ex.printStackTrace
        Future.failed(ex)
    }
  }

}
