package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.SessionMongo
import com.cognitivecreations.dao.mongo.dao.{SessionDao, UserMongoDao}
import com.cognitivecreations.modelconverters.UserConverter
import models.User

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/17/15.
 */
class SessionCoordinator(implicit ec: ExecutionContext) extends CoordinatorBase[SessionMongo]{
  val ALLOWED_FAILURE_COUNT = 5
  val sessionDao = new SessionDao()

  override def findByPrimary(id: UUID): Future[Option[SessionMongo]] = {
    sessionDao.findBySessionId(id)
  }


}
