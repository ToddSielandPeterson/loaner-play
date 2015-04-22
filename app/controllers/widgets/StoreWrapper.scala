package controllers.widgets

import models.UserSession
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Todd Sieland-Peteson on 4/18/15.
 */
object StoreWrapper extends Controller {

  def index(userSession: Option[UserSession] = None, fBody: Html, simpleDbLookups: ExecutionContext) = Action.async { request =>
    implicit val simpleDbLookups1 = simpleDbLookups

    userSession match {
      case None =>
        Future.successful(Ok(views.html.wrappers.content(fBody, UserSession.defaultUserSession())))
      case Some(x) =>
        Future.successful(Ok(views.html.wrappers.content(fBody, x)))
    }
  }

}
