package models

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.SessionMongo

/**
 * Created by Todd Sieland-Peteson on 1/18/15.
 */
case class UserSession(sessionId: UUID,
                       session: Option[SessionMongo] = None,
                       user: Option[User] = None) {
  def isAdmin: Boolean = user.getOrElse(User.defaultUser).admin.getOrElse(false)   //if (user.isDefined) user.get.admin else false
}

object UserSession{
  def defaultUserSession():UserSession = new UserSession(UUID.randomUUID())

}
