package controllers.widgets

import models.UserSession
import play.api.mvc.{Result, Action, Controller}
import scala.concurrent.Future

/**
 * Created by Todd Sieland-Peteson on 1/14/15.
 */
object Footer extends Controller {

  def index(embed: Boolean = false, userSession: Option[UserSession] = None) = Action.async {
    Future.successful(Ok(views.html.widgets.footer_body()))
  }

}
