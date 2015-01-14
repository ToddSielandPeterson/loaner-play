package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.{ProductCoordinator, CategoryCoordinator, UserCoordinator}
import org.joda.time.DateTime
import play.api.libs.concurrent.Akka
import play.api.mvc.Controller
import models._
import play.api.mvc.Action

import scala.concurrent.ExecutionContext
import play.api.Play.current

object LoadDataController extends Controller {
  val hammerId = UUID.randomUUID()
  val sawsId = UUID.randomUUID()
  val taleSawsId = UUID.randomUUID()
  val circleSawsId = UUID.randomUUID()
  val userTodd = UUID.randomUUID()
  val userCyndi = UUID.randomUUID()

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
      new User(Some(userTodd), "Todd", "Sieland-Peterson", "todd@cognitivecreations.com", Some("test"), new Address("1145 Brogdon Dr", None, "Powder Springs", "GA", "30127", None), None),
      new User(Some(userCyndi), "Cyndi", "Tierney", "cyndi@example.com", Some("test"), new Address("1145 Brogdon Dr", None, "Powder Springs", "GA", "30127", None), None))

    users.foreach(userCoordinator.insert)
  }

  def loadCategories(implicit ec: ExecutionContext) = {
    val categoryCoordinator = new CategoryCoordinator()

    val categories = List(
      new Category(categoryId = hammerId, name = "Hammers", uniqueName = "hammers", ordering = 1, parentId = None),
      new Category(categoryId = sawsId, name = "Saws", uniqueName = "saws", ordering = 2, parentId = None),
      new Category(categoryId = taleSawsId, name = "Hammers", uniqueName = "table_saw", ordering = 1, parentId = Some(sawsId)),
      new Category(categoryId = circleSawsId, name = "Hammers", uniqueName = "circle_saw", ordering = 2, parentId = Some(sawsId)))

    categories.foreach(categoryCoordinator.insert)
  }

  def loadProducts(implicit ec: ExecutionContext) = {
    val productCoordinator = new ProductCoordinator()

    val products = List(
      new Product( productId = Some(UUID.randomUUID()), user = userTodd.toString,
        name = "Big Hammer", secondLine = Some("a big hammer"),
        categoryId = Some(hammerId), productType = None, addedDateTime = None, lastUpdate = None,
        pictures = List(), thumbnails = List(), text = "a really <b>really</b> big hammer")
    )

    products.foreach(productCoordinator.insert)
  }
}
