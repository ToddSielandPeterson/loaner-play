package controllers.widgets

import com.cognitivecreations.dao.mongo.coordinator.{UserCoordinator, CategoryCoordinator}
import com.cognitivecreations.utils.SessionUtils
import models.{UserSession, CategoryTree}
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}
import play.api.Play.current

import scala.concurrent.ExecutionContext

/**
 * Created by Todd Sieland-Peteson on 1/14/15.
 */

object Banner extends Controller {

  def index(embed: Boolean = false, userSession: Option[UserSession] = None) = Action { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val bannerLink = "http://www.example.com/banner.jpg"
    val userInfo = userSession.map(ses => ses.user).getOrElse(None)

    if (embed)
      Ok(views.html.widgets.banner(bannerLink, fetchCategoryTree, userInfo))
    else
      Ok(views.html.widgets.banner_full(bannerLink, fetchCategoryTree, userInfo))
  }

  def fetchCategoryTree()(implicit ec: ExecutionContext): CategoryTree = {
    val categoryCoordinator = new CategoryCoordinator()

    categoryCoordinator.fetchCategoryTree
  }

}
