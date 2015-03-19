package controllers.widgets

import com.cognitivecreations.dao.mongo.coordinator.ProductCoordinator
import controllers.widgets.Footer._
import models.{Category, UserSession}
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}
import play.api.Play.current

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Todd Sieland-Peteson on 1/19/15.
 */
object ProductsWidget extends Controller {

  def index(category: Category, embed: Boolean = false, userSession: Option[UserSession] = None) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val productCoordinator = new ProductCoordinator()

    for {
      products <- productCoordinator.findByCategory(category.categoryId.get)
    } yield {
      val productList = for ( prod <- products ) yield prod.copy(thumbnails = List("/noproductimage.jpg"))
      val prodGroup = productList.zipWithIndex.grouped(4).toList
      val session = userSession.getOrElse(UserSession.defaultUserSession())
      if (embed)
        Ok(views.html.widgets.productlist_body(prodGroup, category, session))
      else
        Ok(views.html.widgets.productlist(prodGroup, category, session))
    }
  }

}
