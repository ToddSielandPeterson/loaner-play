package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.{ProductCoordinator, CategoryCoordinator}
import com.cognitivecreations.utils.SessionUtils
import controllers.Application._
import controllers.UserProductController._
import controllers.widgets._
import play.api.libs.concurrent.Akka
import play.api.mvc.{Cookie, Action, Controller}
import play.api.Play.current
import ui.Pagelet

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by tsieland on 1/7/15.
 */
object ProductsController extends Controller {

  def index(categoryName: String) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()
    val categoryCoordinator = new CategoryCoordinator()

    for {
      session <- sessionInfo
      category <- categoryCoordinator.findByCategoryUniqueName(categoryName)

      productHtml <- ProductsWidget.index(category = category.get, embed = true, Some(session))(request)
      productBody <- Pagelet.readBody(productHtml)

      containerHtml <- StoreWrapper.index(Some(session), productBody, simpleDbLookups)(request)
      containerBody <- Pagelet.readBody(containerHtml)
    } yield
      Ok(views.html.shop_filters_left_3cols(session))
  }

  def readOne(productId: String) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()
    val categoryCoordinator = new CategoryCoordinator()
    val productUUID = UUID.fromString(productId)

    for {
      session <- sessionInfo

      productHtml <- ProductWidget.index(productUUID, embed = true, Some(session))(request)
      productBody <- Pagelet.readBody(productHtml)

      containerHtml <- StoreWrapper.index(Some(session), productBody, simpleDbLookups)(request)
      containerBody <- Pagelet.readBody(containerHtml)
    } yield
      Ok(views.html.store(containerBody, session))

  }

}
