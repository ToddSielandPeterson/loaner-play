package controllers.widgets

import controllers.widgets.JavaScripts._
import models.UserSession
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Todd Sieland-Peteson on 4/30/15.
 */
object ShoppingCartPopup extends Controller {

  def index(embed: Boolean = false, userSession: Option[UserSession] = None, simpleDbLookups: ExecutionContext) = Action.async {
    // todo: add some cart stuff here.
    Future.successful(Ok(views.html.widgets.shopping_cart()))
  }

}
