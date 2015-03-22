package controllers


import java.util.UUID

import akka.japi.Option.Some
import com.cognitivecreations.dao.mongo.coordinator.UserCoordinator
import com.cognitivecreations.utils.SessionUtils
import controllers.Application._
import controllers.widgets.{Footer, Banner}

import models._
import models.User._
import play.api.libs.concurrent.Akka
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Cookie, Action, Controller}
import play.api.Play.current
import ui.Pagelet

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.ExecutionContext.Implicits.global

/*
 * Author: Sari Haj Hussein
 */

object UserController extends Controller {


  def index = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val sessionUtils = new SessionUtils(request)
    val userController = new UserCoordinator()
    val sessionInfo = sessionUtils.fetchFutureSessionInfo()

    for {
      session <- sessionInfo
      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)

      headerBody <- Pagelet.readBody(header)
      footerBody <- Pagelet.readBody(footer)
    } yield {
      Ok(views.html.me(headerBody, footerBody, session)).
        withCookies(Cookie("sessioninfo", session.sessionId.toString, Some(86400 * 31)))
    }
  }

  def cleanUpUser(user: User) = {
    if (user.admin.isDefined && user.admin.get) {
      Json.obj("firstName" -> user.firstName,
        "lastName" -> user.lastName,
        "address" -> Json.toJson(user.address),
        "email" -> user.email,
        "userId" -> user.userId,
        "isAdmin" -> user.admin)
    } else {
      Json.obj("firstName" -> user.firstName,
        "lastName" -> user.lastName,
        "address" -> Json.toJson(user.address),
        "email" -> user.email,
        "userId" -> user.userId)
    }
  }

  def loggedInUserInfo = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val sessionUtils = new SessionUtils(request)
    val userController = new UserCoordinator()
    val sessionInfo = sessionUtils.fetchFutureSessionInfo()

    for {
      session <- sessionInfo
    } yield {
      Ok(cleanUpUser(session.user.getOrElse(User.newBlankUser())))
    }
  }

  def list = Action.async {
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val userDao = new UserCoordinator()
    val futureList = userDao.findAll() // convert it to a list of Celebrity
    Await.result(futureList, Duration(5, SECONDS))
    futureList.map {
      user => Ok(Json.toJson(user))
    } // convert it to a JSON and return it
  }

  /** create a celebrity from the given JSON */
  def create() = Action(parse.json) { request =>
    val json = request.body
    val user1 = json.validate[User] match {
      case s: JsSuccess[User] => {
        val user1: User = s.get
        val user = user1.copy(userId = Some(UUID.randomUUID()))
        val userDao = new UserCoordinator()
        val foUser = userDao.insert(user1)
        val error = Await.result(foUser, Duration(5, SECONDS))

        Ok(Json.toJson(if (error.inError) error.message else "ok")) // return the created celebrity in a JSON
      }
      case error: JsError =>
        Ok(Json.toJson(error.toString)) // return the created celebrity in a JSON
    }
    user1
  }
  
  /** retrieve the celebrity for the given id as JSON */
  def show(id: String) = Action.async(parse.empty) { request =>
//    implicit val mongoController = this

    val userDao = new UserCoordinator()
    val futureUser = userDao.findByPrimary(UUID.fromString(id))
    futureUser.map { user => Ok(Json.toJson(user)) }
  }

  def updateUser(user: User): Boolean = {
    val userCoordinator = new UserCoordinator()
    val futureUser = userCoordinator.updateUser(user, user.userId.get)

    val userOut = Await.result[Option[User]](futureUser, Duration.Inf)
    if (userOut.isDefined) {
      val user = userOut.get

    }
    true
  }

  /** update the celebrity for the given id from the JSON body */
  def update(id: String) = Action(parse.json) { request =>
    import play.api.Play.current
    implicit val mongoController = this

    val json = request.body
    json.validate[User] match {
      case s: JsSuccess[User] => {
        if (updateUser(s.get))
          Ok(Json.toJson("Ok"))
        else
          Ok(Json.toJson("Failed"))
      }
      case error: JsError =>
        Ok(Json.toJson(error.toString))
    }
  }

  def delete(id: String) = Action(parse.json) { request =>
    NotImplemented
  }

  def editSelf() = Action { request =>
    NotImplemented
  }

  def editSelfPost() = Action { request =>
    NotImplemented
  }

  def createAccount() = Action { request =>
    NotImplemented
  }

  def createAccountPost() = Action { request =>
    NotImplemented
  }

  def nameFormat(name: String): String = {
    name.capitalize
  }
}
