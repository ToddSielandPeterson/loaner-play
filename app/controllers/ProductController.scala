package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.CategoryCoordinator
import com.cognitivecreations.utils.SessionUtils
import controllers.widgets.{ProductWidget, Banner, Footer, ProductsWidget}
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller, Cookie}
import ui.Pagelet

import scala.concurrent.ExecutionContext

/**
 * Created by tsieland on 1/7/15.
 */
object ProductController extends Controller {

  def index(productId: String) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionUtils = new SessionUtils(request)
    val sessionInfo = sessionUtils.fetchFutureSessionInfo()
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
