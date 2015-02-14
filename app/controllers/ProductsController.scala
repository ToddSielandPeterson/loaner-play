package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.{ProductCoordinator, CategoryCoordinator}
import com.cognitivecreations.utils.SessionUtils
import controllers.Application._
import controllers.UserProductController._
import controllers.widgets.{ProductWidget, ProductsWidget, Footer, Banner}
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

      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)
      productHtml <- ProductsWidget.index(category = category.get, embed = true, Some(session))(request)

      headerBody <- Pagelet.readBody(header)
      productBody <- Pagelet.readBody(productHtml)
      footerBody <- Pagelet.readBody(footer)
    } yield {
      if (category.isDefined)
        Ok(views.html.products(headerBody, footerBody, category.get, productBody, session)).
          withCookies(Cookie("sessioninfo", session.sessionId.toString, Some(86400 * 31)))
      else
        Ok(views.html.products(headerBody, footerBody, category.get, productBody, session)).
          withCookies(Cookie("sessioninfo", session.sessionId.toString, Some(86400 * 31)))
    }
  }

  def readOne(productId: String) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()
    val categoryCoordinator = new CategoryCoordinator()
    val productUUID = UUID.fromString(productId)

    for {
      session <- sessionInfo

      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)
      productHtml <- ProductWidget.index(productUUID, embed = true, Some(session))(request)

      headerBody <- Pagelet.readBody(header)
      productBody <- Pagelet.readBody(productHtml)
      footerBody <- Pagelet.readBody(footer)
    } yield {
      Ok(views.html.product(headerBody, footerBody, productBody, session)).
        withCookies(Cookie("sessioninfo", session.sessionId.toString, Some(86400 * 31)))
    }
  }

}
