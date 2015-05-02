package controllers.widgets

import com.cognitivecreations.dao.mongo.coordinator.CategoryCoordinator
import models.{UserSession, CategoryTree}
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import ui.Pagelet

import scala.concurrent.ExecutionContext

/**
 * Created by Todd Sieland-Peteson on 1/14/15.
 */

object Banner extends Controller {

  def index(embed: Boolean = false, userSession: Option[UserSession] = None) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val bannerLink = "http://www.example.com/banner.jpg"
    val userInfo = userSession.map(ses => ses.user).getOrElse(None)

    for {
      shoppingCart <- ShoppingCartPopup.index(embed, userSession, simpleDbLookups)(request)
      shoppingCartBody <- Pagelet.readBody(shoppingCart)

      category <- CategoryWidget.index(Some(fetchCategoryTree), embed, userSession, simpleDbLookups)(request)
      categoryBody <- Pagelet.readBody(category)
    } yield
      if (embed)
        Ok(views.html.widgets.banner(bannerLink, categoryBody, shoppingCartBody, userInfo))
      else
        Ok(views.html.widgets.banner_full(bannerLink, categoryBody, shoppingCartBody, userInfo))
  }

  def fetchCategoryTree()(implicit ec: ExecutionContext): CategoryTree = CategoryCoordinator().fetchCategoryTree

}
