package controllers.widgets

import com.cognitivecreations.dao.mongo.coordinator.{CategoryCoordinator, ProductCoordinator}
import controllers.widgets.Footer._
import models.{CategoryTree, Category, UserSession}
import play.api.libs.concurrent.Akka
import play.api.mvc.{AnyContent, Action, Controller}
import play.api.Play.current
import play.mvc.Http.Request

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Todd Sieland-Peteson on 3/9/15.
 */
object CategoryWidget extends Controller {

  def index(categoryTree: Option[CategoryTree], embed: Boolean = false, userSession: Option[UserSession] = None, simpleDbLookups: ExecutionContext)
           = Action.async { request =>
    implicit val simpleDbLookups1 = simpleDbLookups

    val categoryT = categoryTree.getOrElse { CategoryCoordinator().buildCategoryTree }

    if (embed)
      Future(Ok(views.html.widgets.category_body(categoryT, userSession.get)))
    else
      Future(Ok(views.html.widgets.category(categoryT, userSession.get)))
  }

}