package controllers

import akka.japi.Option.Some
import com.cognitivecreations.dao.mongo.coordinator.UserCoordinator
import com.cognitivecreations.utils.SessionUtils
import controllers.widgets.{Footer, Banner}
import models.UserSession
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}
import ui.Pagelet
import play.api.Play.current

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 2/2/15.
 */
object UserDesktopController extends Controller {

  def index = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionUtils = new SessionUtils(request)
    val userController = new UserCoordinator()
    val sessionInfo = sessionUtils.fetchFutureSessionInfo()

    for {
      session <- sessionInfo
      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)
      desktop <- desktop(session)(request)

      headerBody <- Pagelet.readBody(header)
      footerBody <- Pagelet.readBody(footer)
      desktopBody <- Pagelet.readBody(desktop)
    } yield {
      Ok(views.html.user.user_template(headerBody, desktopBody, footerBody, session))
    }
  }

  def desktop(userSession: UserSession) = Action { implicit request =>
//    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    Ok(views.html.user.desktop_body(userSession, None))

  }
}
