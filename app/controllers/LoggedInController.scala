package controllers

import java.util.UUID

import com.cognitivecreations.utils.SessionUtils
import models.User
import org.joda.time.DateTime
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 6/1/15.
 */
trait LoggedInController {
  def loggedInUser()(implicit request: Request[AnyContent], simpleDbLookups: ExecutionContext): Future[User] = {
    for {
      session <- SessionUtils(request).fetchFutureSessionInfo()
    } yield {
      if (session.user.isEmpty || session.user.get.userId.isEmpty) {
        throw new NotLoggedInException(None)
      } else {
        session.user.get
      }
    }
  }
  def loggedInAdminUser()(implicit request: Request[AnyContent], simpleDbLookups: ExecutionContext): Future[User] = {
    loggedInUser.map {
      case user if user.admin.getOrElse(false) => user
      case user => throw new NotLoggedInAsAdminException(user.userId)
    }
  }

}

class NotLoggedInException(userId: Option[UUID]) extends Exception
class NotLoggedInAsAdminException(userId: Option[UUID]) extends NotLoggedInException(userId)

