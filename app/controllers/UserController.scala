package controllers


import java.util.UUID

import akka.japi.Option.Some
import com.cognitivecreations.dao.mongo.coordinator.{CategoryCoordinator, UserCoordinator}
import com.cognitivecreations.utils.SessionUtils
import controllers.Application._
import controllers.CategoriesController._
import controllers.widgets.{Footer, Banner}

import models._
import models.User._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.concurrent.Akka
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Cookie, Action, Controller}
import play.api.Play.current
import ui.Pagelet

import scala.concurrent.duration._
import scala.concurrent.{Future, ExecutionContext, Await}
import scala.concurrent.ExecutionContext.Implicits.global

/*
 * Author: Sari Haj Hussein
 */

object UserController extends Controller with LoggedInController {

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

  def copyUser(user: User, input: UserData): User = {
    user.copy(
      firstName = input.firstName,
      lastName = input.lastName,
      admin = input.admin,
      email = input.email,
      address = user.address.copy(
        addressLine1 = input.addressLine1,
        addressLine2 = input.addressLine2,
        city = input.city,
        state = input.state,
        zip = input.zip,
        country = input.country
      )
    )
  }

  /** update the celebrity for the given id from the JSON body */
  def update(id: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val userFormInputHolder = UserForm.bindFromRequest()

    if (userFormInputHolder.hasErrors) {
      Future.successful(Ok(Json.toJson(Error(userFormInputHolder.toString))))
    } else {
      val userCoordinator = new UserCoordinator()
      val sessionUtils = new SessionUtils(request)
      val sessionInfo = sessionUtils.fetchFutureSessionInfo()

      for {
        session <- sessionInfo
        adminUser = session.user.getOrElse(User.newBlankUser())
        inputUser = userFormInputHolder.get
        uuid = if (inputUser.userId.isDefined) UUID.fromString(inputUser.userId.get) else UUID.randomUUID()
        user <- userCoordinator.findByPrimary(uuid)
      } yield {
        if (adminUser.userId.isEmpty || !adminUser.admin.getOrElse(false)) {
          Ok(Json.toJson(Error("You are not logged in or do no have permission")))
        } else {
          if (user.isDefined) {
            val updatedUser = copyUser(user.get, userFormInputHolder.get)
            userCoordinator.updateUser(updatedUser, updatedUser.userId.get)
            Ok(Json.toJson(user))
          } else {
            Ok(Json.toJson(Error("User does not exist")))
          }
        }
      }
    }
  }

  /** update the celebrity for the given id from the JSON body */
  def add() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val userFormInputHolder = UserAddForm.bindFromRequest()

    if (userFormInputHolder.hasErrors) {
      Future.successful(Ok(Json.toJson(Error(userFormInputHolder.toString))))
    } else {
      val userCoordinator = new UserCoordinator()
      val sessionUtils = new SessionUtils(request)
      val sessionInfo = sessionUtils.fetchFutureSessionInfo()

      for {
        session <- sessionInfo
        adminUser = session.user.getOrElse(User.newBlankUser())
        inputUser = userFormInputHolder.get
        uuid = if (inputUser.userId.isDefined) UUID.fromString(inputUser.userId.get) else UUID.randomUUID()
        user <- userCoordinator.findByPrimary(uuid)
      } yield {
        if (adminUser.userId.isEmpty || !adminUser.admin.getOrElse(false)) {
          Ok(Json.toJson(Error("You are not logged in or do no have permission")))
        } else {
          if (user.isEmpty) {
            val input = userFormInputHolder.get
            val updatedUser = copyUser(User.newBlankUser(), userFormInputHolder.get).copy( userId = Some(UUID.randomUUID()) )
            userCoordinator.updateUser(updatedUser, updatedUser.userId.get)
            Ok(Json.toJson(user))
          } else {
            Ok(Json.toJson(Error("User Already Exists")))
          }
        }
      }
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

  case class UserData(userId: Option[String],   // uuid for user (unique)
                      firstName: String,
                      lastName: String,
                      email: String,
                      password: Option[String],
                      addressLine1: String,
                      addressLine2: Option[String],
                      city: String,
                      state: String,
                      zip: String,
                      country: Option[String],
                      admin: Option[Boolean]) {
    def toNewUser: User = {
      copyUserUser(User.newBlankUser).copy(
        password = password,
        passwordAgain = password
      )
    }
    def copyUserUser(user: User):User = {
      user.copy(
        firstName = firstName,
        lastName = lastName,
        admin = admin,
        email = email,
        address = user.address.copy(
          addressLine1 = addressLine1,
          addressLine2 = addressLine2,
          city = city,
          state = state,
          zip = zip,
          country = country
        )
      )
    }
  }

  val UserForm = Form(
    mapping(
      "userId" -> optional(text),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "password" -> optional(text),
      "address.addressLine1" -> nonEmptyText,
      "address.addressLine2" -> optional(text),
      "address.city" -> nonEmptyText,
      "address.state" -> nonEmptyText,
      "address.zip" -> nonEmptyText,
      "address.country" -> optional(text),
      "isAdmin" -> optional(boolean)
    )(UserData.apply)(UserData.unapply)
  )

  case class UserAddData(name: String,
                             uniqueName: String,
                             ordering: Int = 0, // link to unique User id
                             parentId: Option[String] = None)

  val UserAddForm = Form(
    mapping(
      "userId" -> optional(text),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "password" -> optional(text),
      "address.addressLine1" -> nonEmptyText,
      "address.addressLine2" -> optional(text),
      "address.city" -> nonEmptyText,
      "address.state" -> nonEmptyText,
      "address.zip" -> nonEmptyText,
      "address.country" -> optional(text),
      "isAdmin" -> optional(boolean)
    )(UserData.apply)(UserData.unapply)
  )


}
