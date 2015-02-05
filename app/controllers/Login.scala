package controllers

import com.cognitivecreations.dao.mongo.coordinator.UserCoordinator
import com.cognitivecreations.utils.SessionUtils
import controllers.widgets.{Banner, Footer}
import play.api.Play.current
import play.api.data.Form

import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages

import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller, Cookie}
import ui.Pagelet

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/14/15.
 */
object Login extends Controller {

  case class UserData(username: String, password: String)

  val userForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserData.apply)(UserData.unapply)
  )

  case class LostPasswordData(username: String)

  val lostPasswordForm = Form(
    mapping(
      "username" -> nonEmptyText
    )(LostPasswordData.apply)(LostPasswordData.unapply)
  )

  def index() = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionUtils = new SessionUtils(request)
    val sessionInfo = sessionUtils.fetchFutureSessionInfo()

    for {
      session <- sessionInfo
      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)

      headerBody <- Pagelet.readBody(header)
      footerBody <- Pagelet.readBody(footer)
    } yield {

      Ok(views.html.login(headerBody, footerBody, session, None)).
        withCookies(Cookie("sessioninfo", session.sessionId.toString, Some(86400 * 31)))
    }
  }

  def post() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val userCoordinator = new UserCoordinator()
    val userFormInputHolder = userForm.bindFromRequest()
    val sessionUtils = new SessionUtils(request)

    val sessionInfo = sessionUtils.fetchFutureSessionInfo()
    for {
      session <- sessionInfo
      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)
      u <- {
        if (!userFormInputHolder.hasErrors) {
          val userFormInput = userFormInputHolder.get
          userCoordinator.findUserByIdAndPassword(userFormInput.username.toLowerCase, userFormInput.password)
        } else {
          Future.successful(None)
        }
      }
      headerBody <- Pagelet.readBody(header)
      footerBody <- Pagelet.readBody(footer)
    } yield {
      val newSession = session.copy(user = u)
      if (u.isDefined) {
        sessionUtils.saveSession(newSession.copy(user = u)) // add user to session
        Redirect("/").
          withCookies(Cookie("sessioninfo", session.sessionId.toString, Some(86400 * 31)))
      } else {
        sessionUtils.saveSession(newSession.copy(user = None)) // reset the user
        if (userFormInputHolder.hasErrors)
          Ok(views.html.login(headerBody, footerBody, session, Some(userFormInputHolder)))
        else {
          userForm.withError(FormError("all", Messages("login.error.nouser")))
          Ok(views.html.login(headerBody, footerBody, session,
            Some(userForm.withError(FormError("login", "error.nouser")))))
        }
      }
    }
  }

//  def popup() = Action.async { request =>
//    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
//
//    val sessionUtils = new SessionUtils(request)
//    val sessionInfo = sessionUtils.fetchFutureSessionInfo()
//
//    for {
//      session <- sessionInfo
//    } yield {
//
//      Ok(views.html.popups.loginpopup_body(session)).withCookies(Cookie("sessioninfo", session.sessionId.toString, Some(86400 * 31)))
//    }
//  }

  def lostPassword() = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionUtils = new SessionUtils(request)
    val sessionInfo = sessionUtils.fetchFutureSessionInfo()

    for {
      session <- sessionInfo
      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)

      headerBody <- Pagelet.readBody(header)
      footerBody <- Pagelet.readBody(footer)
    } yield {

      Ok(views.html.lostpassword(headerBody, footerBody, session, None))
    }
  }

  def lostPasswordPost() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val userCoordinator = new UserCoordinator()
    val userFormInputHolder = lostPasswordForm.bindFromRequest()
    val sessionUtils = new SessionUtils(request)

    val sessionInfo = sessionUtils.fetchFutureSessionInfo()
    for {
      session <- sessionInfo
      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)
      u <- {
        if (!userFormInputHolder.hasErrors) {
          val userFormInput = userFormInputHolder.get
          userCoordinator.findUserByUserName(userFormInput.username.toLowerCase)
        } else {
          Future.successful(None)
        }
      }
      headerBody <- Pagelet.readBody(header)
      footerBody <- Pagelet.readBody(footer)
    } yield {
      if (u.isDefined) {
        // send email
        userForm.withError(FormError("all", Messages("login.error.nouser")))
        Ok(views.html.lostpassword(headerBody, footerBody, session, None))
      } else {
        if (userFormInputHolder.hasErrors)
          Ok(views.html.lostpassword(headerBody, footerBody, session, Some(userFormInputHolder)))
        else {
          userForm.withError(FormError("all", Messages("login.error.nouser")))
          Ok(views.html.lostpassword(headerBody, footerBody, session,
            Some(lostPasswordForm.withError(FormError("login", "error.nouser")))))
        }
      }
    }
  }

  def logout() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionUtils = new SessionUtils(request)

    val sessionInfo = sessionUtils.fetchFutureSessionInfo()
    for {
      session <- sessionInfo
      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)

      headerBody <- Pagelet.readBody(header)
      footerBody <- Pagelet.readBody(footer)
    } yield {
      if (session.user.isDefined) {
        sessionUtils.saveSession(session.copy(user = None))
      }
      Redirect("/")
    }
  }

}
