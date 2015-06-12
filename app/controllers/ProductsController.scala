package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.{ProductCoordinator, CategoryCoordinator}
import com.cognitivecreations.utils.SessionUtils
import controllers.widgets._
import models.{Category, Product}
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import ui.Pagelet

import scala.concurrent.ExecutionContext

/**
 * Created by tsieland on 1/7/15.
 */
object ProductsController extends Controller with LoggedInController {

  def categoriesCategoryId(category: Option[Category]): UUID =
    category.getOrElse(Category.newCategory).categoryId.getOrElse(UUID.randomUUID())

  def productCategoryId(product: Option[Product]): UUID =
    if (product.isDefined) product.get.categoryId.getOrElse(UUID.randomUUID())
    else UUID.randomUUID()

  def index(categoryName: String) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()
    val categoryCoordinator = new CategoryCoordinator()
    val productCoordinator = new ProductCoordinator()

    for {
      session <- sessionInfo
      header <- Banner.index(embed = true, Some(session))(request)
      category <- categoryCoordinator.findByCategoryUniqueName(categoryName)
      products <- productCoordinator.findByCategory(categoriesCategoryId(category))

      headerBody <- Pagelet.readBody(header)
      footer <- Footer.index(embed = true, Some(session))(request)
      footerBody <- Pagelet.readBody(footer)
      javascripts <- JavaScripts.index(Some(session))(request)
      javascriptsBody <- Pagelet.readBody(javascripts)
    } yield
      Ok(views.html.shop_filters_left_3cols(session, products, headerBody, footerBody, javascriptsBody))
  }

  def readOne(productId: String) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()
    val categoryCoordinator = new CategoryCoordinator()
    val productUUID = UUID.fromString(productId)
    val productCoordinator = new ProductCoordinator()

    for {
      session <- sessionInfo
      header <- Banner.index(embed = true, Some(session))(request)
      product <- productCoordinator.findByPrimary(productUUID)
      category <- categoryCoordinator.findByCategoryUniqueName(productCategoryId(product).toString)

      headerBody <- Pagelet.readBody(header)
      footer <- Footer.index(embed = true, Some(session))(request)
      footerBody <- Pagelet.readBody(footer)
      javascripts <- JavaScripts.index(Some(session))(request)
      javascriptsBody <- Pagelet.readBody(javascripts)
    } yield
      if (product.isDefined)
        Ok(views.html.shop_single_item_v1(session, product.get, headerBody, footerBody, javascriptsBody))
      else
        Ok(views.html.shop_single_item_v1(session, product.get, headerBody, footerBody, javascriptsBody))
  }

}
