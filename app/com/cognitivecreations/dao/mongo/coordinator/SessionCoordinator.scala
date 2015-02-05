package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.SessionMongo
import com.cognitivecreations.dao.mongo.dao.SessionDao
import models.UserSession
import org.joda.time.DateTime
import reactivemongo.bson.{BSONObjectID, BSONDocument}

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
      if (sess.isDefined)
        Some(new UserSession(id, sess, user))
      else
        None
  }

  def insertNewSession(id: UUID): UserSession = {
    val sess = new SessionMongo(sessionId = id, loggedInRecently = Some(false), activeDate = DateTime.now())
    sessionDao.insert(sess)
    new UserSession(id, Some(sess))
  }

  def update(session: UserSession) = {
    for {
      sess <- sessionDao.findBySessionId(session.sessionId)
    } yield
      if (sess.isDefined) {
        val newSes = sess.get.copy(userId = session.user.map(u => u.userId).getOrElse(None))
        sessionDao.upsert(session.sessionId, sess.get, newSes)
        println(s"logged in as user ${session.user.get}")
      } else {
        println(s"Filed login of user ${session.user.get}")
      }
  }

}
