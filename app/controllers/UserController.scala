package controllers


import java.util.UUID

import akka.japi.Option.Some
import com.cognitivecreations.dao.mongo.coordinator.UserCoordinator

import models._
import models.User._
import play.api.libs.concurrent.Akka
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.Play.current

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.ExecutionContext.Implicits.global

/*
 * Author: Sari Haj Hussein
 */

object UserController extends Controller {

  def index = Action.async {
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
    Ok
  }

  def nameFormat(name: String): String = {
    name.capitalize
  }
}
