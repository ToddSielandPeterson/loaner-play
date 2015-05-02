package controllers.widgets

import models.UserSession
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

/**
 * Created by Todd Sieland-Peteson on 1/14/15.
 */
object JavaScripts extends Controller {

  def index(userSession: Option[UserSession] = None) = Action.async {
    Future.successful(Ok(views.html.widgets.javascript_files()))
  }

}
