package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.{CategoryFlat, CategoryCoordinator}
import com.cognitivecreations.utils.SessionUtils
import controllers.widgets.{CategoryWidget, Footer, Banner}
import models.{Error, Category, FlatCategory}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import ui.Pagelet

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 2/17/15.
 */
object CategoriesController extends Controller with LoggedInController {
  import models.Product._

  /* rest Calls */
  def categoriesFlat() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val categoryCoordinator = new CategoryCoordinator()
    val categoryTree = categoryCoordinator.buildCategoryTree
    val categoryThing = CategoryFlat.applyAll(categoryTree)
    val flatCategories = categoryThing.map{x => new FlatCategory(x.id.toString, x.categoryPath.reduce[String]((acc,x) => acc + " -> " + x))}

    Future.successful(Ok(Json.toJson(flatCategories)))
  }

  def allCategoriesFlat() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val categoryCoordinator = new CategoryCoordinator()
    val categoryTree = categoryCoordinator.buildCategoryTree
    val categoryThing = CategoryFlat.buildAllList(categoryTree)
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

  def optStringToOptUUID(os: Option[String]): Option[UUID] = {
    os.map(s => UUID.fromString(s))
  }

  def categoryUpdate(id: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val categoryCoordinator = new CategoryCoordinator()
    val categoryFormInputHolder = categoryForm.bindFromRequest()
    val sessionUtils = new SessionUtils(request)
    val sessionInfo = sessionUtils.fetchFutureSessionInfo()

    if (categoryFormInputHolder.hasErrors) {
      Future.successful(Ok(Json.toJson(Error(categoryFormInputHolder.toString))))
    } else {
      // valid
      for {
        session <- sessionInfo
      } yield {
        if (session.user.isEmpty || session.user.get.userId.isEmpty) {
          Ok(Json.toJson(Error("You are not logged in")))
        } else {
          val categoryIn = categoryFormInputHolder.get
          val category = Category(categoryId = optStringToOptUUID(categoryIn.categoryId),
            name = categoryIn.name,
            uniqueName = categoryIn.uniqueName,
            ordering = categoryIn.ordering,
            parentId = categoryIn.parentId.map(UUID.fromString))
          categoryCoordinator.update(category)
          Ok(Json.toJson(category))
        }
      }
    }
  }

  def categoryDelete(id: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val categoryCoordinator = new CategoryCoordinator()
    val sessionUtils = new SessionUtils(request)
    val sessionInfo = sessionUtils.fetchFutureSessionInfo()

    // valid
    for {
      session <- sessionInfo
    } yield {
      if (session.user.isEmpty || session.user.get.userId.isEmpty) {
        Ok(Json.toJson(Error("You are not logged in")))
      } else {
        categoryCoordinator.delete(UUID.fromString(id))
        Ok(Json.toJson(Map("status" -> "OK")))
      }
    }
  }

  def categoryAdd() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val categoryCoordinator = new CategoryCoordinator()
    val categoryFormInputHolder = categoryAddForm.bindFromRequest()
    val sessionUtils = new SessionUtils(request)
    val sessionInfo = sessionUtils.fetchFutureSessionInfo()

    if (categoryFormInputHolder.hasErrors) {
      Future.successful(Ok(Json.toJson(Error(categoryFormInputHolder.toString))))
    } else {
      // valid
      for {
        session <- sessionInfo
      } yield {
        if (session.user.isEmpty || session.user.get.userId.isEmpty) {
          Ok(Json.toJson(Error("You are not logged in")))
        } else {
          val categoryIn = categoryFormInputHolder.get
          val category = Category(categoryId = None,
            name = categoryIn.name,
            uniqueName = categoryIn.uniqueName,
            ordering = categoryIn.ordering,
            parentId = categoryIn.parentId.map(UUID.fromString))
          categoryCoordinator.insert(category)
          Ok(Json.toJson(category))
        }
      }
    }

  }


  case class CategoryData(categoryId: Option[String] = None,
                          name: String,
                          uniqueName: String,
                          ordering: Int = 0, // link to unique category id
                          parentId: Option[String] = None)

  val categoryForm = Form(
    mapping(
      "categoryId" -> optional(text),
      "name" -> nonEmptyText,
      "uniqueName" -> nonEmptyText,
      "ordering" -> number,
      "parentId" -> optional(text)
    )(CategoryData.apply)(CategoryData.unapply)
  )

  case class CategoryAddData(name: String,
                          uniqueName: String,
                          ordering: Int = 0, // link to unique category id
                          parentId: Option[String] = None)

  val categoryAddForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "uniqueName" -> nonEmptyText,
      "ordering" -> number,
      "parentId" -> optional(text)
    )(CategoryAddData.apply)(CategoryAddData.unapply)
  )


}
