package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.{ProductCoordinator, CategoryCoordinator, UserCoordinator}
import com.cognitivecreations.dao.mongo.dao.SessionDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.SessionMongo
import play.api.libs.concurrent.Akka
import play.api.mvc.{Cookie, Controller, Action}
import models._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import play.api.Play.current

object LoadDataController extends Controller with LoggedInController {
  val hammerId = UUID.randomUUID()
  val sawsId = UUID.randomUUID()
  val taleSawsId = UUID.randomUUID()
  val circleSawsId = UUID.randomUUID()
  val userTodd = UUID.randomUUID()
  val userCyndi = UUID.randomUUID()

//  def loader() = Action { request =>
//    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
//
//    val optCookieId = request.cookies.get("sessioninfo")
//    val sessionId = optCookieId.getOrElse(UUID.randomUUID())
//
////    loadUsers
////    loadCategories
////    loadProducts
//
//    if (optCookieId.isDefined)
//      Ok(views.html.loader())
//    else
//      Ok(views.html.loader()).withCookies(Cookie("sessioninfo", sessionId.toString, Some(86400 * 31)))
//  }
//
//  def sessionTest() = Action {
//    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
//
//    val sessionDao = new SessionDao()
//    val session = new SessionMongo(sessionId = UUID.randomUUID())
//
//    sessionDao.insert(session)
//
//    Ok(views.html.loader())
//  }
//
//  def sessionPullTest() = Action {
//    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
//
//    val sessionDao = new SessionDao()
//
//    for (x <- sessionDao.findBySessionId(UUID.fromString("853e8c86-f3c8-48c1-b9ef-2227467da91d")))
//      yield x
//
//    Ok(views.html.loader())
//  }
//
//  def loadUsers = Action{
//    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
//
//    val userCoordinator = new UserCoordinator
//
//    val users = List(
//      new User(userId = Some(userTodd), firstName = "Todd", lastName = "Sieland-Peterson", email = "todd@cognitivecreations.com", password = Some("test"),
//        passwordAgain = None, address = new Address("1145 Brogdon Dr", None, "Powder Springs", "GA", "30127", None)),
//      new User(userId = Some(userCyndi), firstName = "Cyndi", lastName = "Tierney", email = "cyndi@example.com", password = Some("test"),
//        passwordAgain = None, address = new Address("1145 Brogdon Dr", None, "Powder Springs", "GA", "30127", None)))
//
//    users.foreach(userCoordinator.insert)
//
//    Ok(views.html.loader())
//  }
//
//  def loadCategories = Action {
//    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
//    val categoryCoordinator = new CategoryCoordinator()
//
//    val categories = List(
//      new Category(categoryId = Some(hammerId), name = "Hammers", uniqueName = "hammers", ordering = 1, parentId = None),
//      new Category(categoryId = Some(sawsId), name = "Saws", uniqueName = "saws", ordering = 2, parentId = None),
//      new Category(categoryId = Some(taleSawsId), name = "Table Saw", uniqueName = "table_saw", ordering = 1, parentId = Some(sawsId)),
//      new Category(categoryId = Some(circleSawsId), name = "Circular Saw", uniqueName = "circle_saw", ordering = 2, parentId = Some(sawsId)))
//
//    categories.foreach(categoryCoordinator.insert)
//
//    categories.foreach{ x =>
//      val category1 = Await.result(categoryCoordinator.findByCategoryUniqueName(x.uniqueName), Duration(5000, SECONDS))
//      category1.map(cat => println(s"found ${cat.name}"))
//    }
//
//    Ok(views.html.loader())
//  }
//
//  def loadProducts = Action{
//    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
//    val productCoordinator = new ProductCoordinator()
//
//    val products = List(
//      new Product( productId = Some(UUID.randomUUID()), userId = Some(userTodd),
//        name = "Big Hammer", secondLine = Some("a big hammer"),
//        categoryId = Some(hammerId), productType = None, addedDateTime = None, lastUpdate = None,
//        pictures = List(), thumbnails = List(), text = "a really <b>really</b> big hammer")
//    )
//
//    products.foreach(productCoordinator.insert)
//
//    Ok(views.html.loader())
//  }
}
