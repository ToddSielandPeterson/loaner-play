package controllers.widgets

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.{CategoryCoordinator, ProductCoordinator}
import models.UserSession
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}
import play.api.Play.current

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/19/15.
 */
object ProductWidget extends Controller {

  def index(productId: UUID, embed: Boolean = false, userSession: Option[UserSession] = None) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val productCoordinator = new ProductCoordinator()
    val categoryCoordinator = new CategoryCoordinator()

    for {
      product <- productCoordinator.findByPrimary(productId)
      category <- if (product.isDefined) categoryCoordinator.findByPrimary(product.get.categoryId)
        else Future.successful(None)
    } yield {
      val session = userSession.getOrElse(UserSession.defaultUserSession())
      if (product.isDefined && category.isDefined)
        if (embed)
          Ok(views.html.widgets.product_body(product.get, category.get, session))
        else
          Ok(views.html.widgets.product(product.get, category.get, session))
      else
        if (embed)
          Ok(views.html.widgets.productinvalid_body(productId.toString, session))
        else
          Ok(views.html.widgets.productinvalid(productId.toString, session))
    }
  }

}
