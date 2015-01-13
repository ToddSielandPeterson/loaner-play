package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.UserCoordinator
import play.api.libs.concurrent.Akka
import play.api.mvc.Controller
import models._
import play.api.mvc.Action

import scala.concurrent.ExecutionContext
import play.api.Play.current

object LoadDataController extends Controller {

  def index() = Action {
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

//    val products = List(
//      Product()
//    )

    Ok(views.html.index())
  }

  def loadUsers(implicit ec: ExecutionContext) = {
    val userCoordinator = new UserCoordinator

    val users = List(
      new User(Some(UUID.randomUUID()), "Todd", "Sieland-Peterson", "todd@cognitivecreations.com", Some("test"), new Address("1145 Brogdon Dr", None, "Powder Springs", "GA", "30127", None), None),
      new User(Some(UUID.randomUUID()), "Cyndi", "Tierney", "cyndi@example.com", Some("test"), new Address("1145 Brogdon Dr", None, "Powder Springs", "GA", "30127", None), None))

    val x = for {
      user <- users
    } yield userCoordinator.insert(user)
  }

  def loadCategoreis(implicit ec: ExecutionContext) = {
    val categoryCoordinator = new CategoryCoordinator

    val Categories = List(
      new User(Some(UUID.randomUUID()), "Todd", "Sieland-Peterson", "todd@cognitivecreations.com", Some("test"), new Address("1145 Brogdon Dr", None, "Powder Springs", "GA", "30127", None), None),
      new User(Some(UUID.randomUUID()), "Cyndi", "Tierney", "cyndi@example.com", Some("test"), new Address("1145 Brogdon Dr", None, "Powder Springs", "GA", "30127", None), None))

    val x = for {
      user <- users
    } yield userCoordinator.insert(user)
  }

  def loadProducts(implicit ec: ExecutionContext) = {
    val userCoordinator = new UserCoordinator

    val users = List(
      new User(Some(UUID.randomUUID()), "Todd", "Sieland-Peterson", "todd@cognitivecreations.com", Some("test"), new Address("1145 Brogdon Dr", None, "Powder Springs", "GA", "30127", None), None),
      new User(Some(UUID.randomUUID()), "Cyndi", "Tierney", "cyndi@example.com", Some("test"), new Address("1145 Brogdon Dr", None, "Powder Springs", "GA", "30127", None), None))

    val x = for {
      user <- users
    } yield userCoordinator.insert(user)
  }
}
