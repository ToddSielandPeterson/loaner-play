package controllers

import com.cognitivecreations.dao.mongo.coordinator.UserCoordinator
import com.cognitivecreations.utils.SessionUtils
import controllers.widgets.{Footer, Banner}
import play.api.libs.concurrent.Akka
import play.api.mvc.{Cookie, Action, Controller}
import ui.Pagelet

import scala.concurrent.ExecutionContext
import play.api.Play.current


/*
 * Author: Sari Haj Hussein
 */

object Application extends Controller {
  
  /** serve the index page app/views/index.scala.html */
  def index(any: String) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val userController = new UserCoordinator()
    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()

    for {
      session <- sessionInfo
      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)

      headerBody <- Pagelet.readBody(header)
      footerBody <- Pagelet.readBody(footer)
    } yield {
      Ok(views.html.index(headerBody, footerBody, session)).
        withCookies(Cookie("sessioninfo", session.sessionId.toString, Some(86400 * 31)))
    }

  }
  
}