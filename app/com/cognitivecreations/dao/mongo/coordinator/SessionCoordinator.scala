package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.SessionMongo
import com.cognitivecreations.dao.mongo.dao.SessionDao
import models.UserSession
import org.joda.time.DateTime

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/17/15.
 */
class SessionCoordinator(implicit ec: ExecutionContext) extends CoordinatorBase[UserSession]{
  val ALLOWED_FAILURE_COUNT = 5
  val sessionDao = new SessionDao()
  val userCoordinator = new UserCoordinator()

  override def findByPrimary(id: UUID): Future[Option[UserSession]] = {
    for {
      sess <- sessionDao.findBySessionId(id)
      user <- userCoordinator.findByPrimary(if (sess.isDefined) sess.get.userId else None)
    } yield
      Some(new UserSession(id, sess, user))
  }

  def insertNewSession(id: UUID): UserSession = {
    val sess = new SessionMongo(sessionId = id, loggedInRecently = Some(false), activeDate = DateTime.now())
    sessionDao.insert(sess)
    new UserSession(id, Some(sess))
  }

}
