package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.{CategoryFlat, CategoryCoordinator}
import com.cognitivecreations.utils.SessionUtils
import controllers.ProductsController._
import controllers.widgets.{CategoryWidget, ProductsWidget, Footer, Banner}
import models.FlatCategory
import play.api.libs.concurrent.Akka
import play.api.Play.current
import play.api.libs.json.Json
import play.api.mvc.{Cookie, Action, Controller}
import ui.Pagelet

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 2/17/15.
 */
object CategoriesController extends Controller {
  import models.Product._

  /* web page calls */
  def index() = Action.async{ implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()
    val categoryCoordinator = new CategoryCoordinator()

    for {
      session <- sessionInfo
      category = categoryCoordinator.buildCategoryTree

      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)
      categoriesHtml <- CategoryWidget.index(category = category, embed = true, Some(session))(request)

      headerBody <- Pagelet.readBody(header)
      categoriesBody <- Pagelet.readBody(categoriesHtml)
      footerBody <- Pagelet.readBody(footer)
    } yield {
        Ok(views.html.categories(headerBody, footerBody, categoriesBody, session))
    }
  }

  /* rest Calls */
  def categoriesFlat() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val categoryCoordinator = new CategoryCoordinator()
    val categoryTree = categoryCoordinator.buildCategoryTree
    val categoryThing = CategoryFlat.applyAll(categoryTree)
    val flatCategories = categoryThing.map{x => new FlatCategory(x.id.toString, x.categoryPath.reduce[String]((acc,x) => acc + " -> " + x))}

    Future.successful(Ok(Json.toJson(flatCategories)))
  }

  def category(id: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val categoryCoordinator = new CategoryCoordinator()
    for {
      cat <- categoryCoordinator.findByPrimary(UUID.fromString(id))
    } yield Ok(Json.toJson(cat))
  }
}
