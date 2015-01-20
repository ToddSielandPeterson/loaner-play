package models

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.SessionMongo

/**
 * Created by Todd Sieland-Peteson on 1/18/15.
 */
case class UserSession(sessionId: UUID,
                       session: Option[SessionMongo] = None,
                       user: Option[User] = None)

object UserSession{
  def defaultUserSession():UserSession = { new UserSession(UUID.randomUUID()) }
}
