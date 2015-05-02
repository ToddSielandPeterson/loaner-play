package controllers

import play.api.Play.current
import com.cognitivecreations.dao.mongo.coordinator.CategoryCoordinator
import com.cognitivecreations.utils.SessionUtils
import controllers.CategoriesController._
import controllers.widgets.{StoreWrapper, CategoryWidget, Footer, Banner}
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}
import ui.Pagelet

import scala.concurrent.ExecutionContext

/**
 * Created by Todd Sieland-Peteson on 4/18/15.
 */
object StoreCategoryController extends Controller {
  import models.Product._

  /* web page calls */
  def index() = Action.async{ implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()
    val categoryCoordinator = new CategoryCoordinator()

    for {
      session <- sessionInfo
      category = categoryCoordinator.buildCategoryTree

      categoriesHtml <- CategoryWidget.index(categoryTree = Some(category), embed = true, Some(session), simpleDbLookups)(request)
      categoriesBody <- Pagelet.readBody(categoriesHtml)

      containerHtml <- StoreWrapper.index(Some(session), categoriesBody, simpleDbLookups)(request)
      containerBody <- Pagelet.readBody(containerHtml)
    } yield {
      Ok(views.html.store(containerBody, session))
    }
  }



}
