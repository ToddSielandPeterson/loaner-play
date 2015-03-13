package controllers.widgets

import com.cognitivecreations.dao.mongo.coordinator.ProductCoordinator
import controllers.widgets.Footer._
import models.{CategoryTree, Category, UserSession}
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}
import play.api.Play.current

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Todd Sieland-Peteson on 3/9/15.
 */
object CategoryWidget extends Controller {

  def index(category: CategoryTree, embed: Boolean = false, userSession: Option[UserSession] = None) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    if (embed)
      Future(Ok(views.html.widgets.category_body(category, userSession.get)))
    else
      Future(Ok(views.html.widgets.category(category, userSession.get)))
  }

}