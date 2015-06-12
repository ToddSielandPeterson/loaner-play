package controllers

import controllers.CategoriesController._
import play.api.libs.json.Json
import play.api.mvc.{Controller, Action}

import scala.concurrent.Future

/**
 * Created by Todd Sieland-Peteson on 5/4/15.
 */
object GalleryController extends Controller with LoggedInController {

  def userImages() = Action.async { implicit request =>

    Future.successful(Ok(Json.toJson(Map("" -> ""))))
  }

  def allImages() = Action.async { implicit request =>

    Future.successful(Ok(Json.toJson(Map("" -> ""))))
  }

  def galleryItem(id: String) = Action.async { implicit request =>

    Future.successful(Ok(Json.toJson(Map("" -> ""))))
  }

  def galleryUpdate(id: String) = Action.async { implicit request =>

    Future.successful(Ok(Json.toJson(Map("" -> ""))))
  }

  def galleryDelete(id: String) = Action.async { implicit request =>

    Future.successful(Ok(Json.toJson(Map("" -> ""))))
  }

  def galleryAdd() = Action.async { implicit request =>

    Future.successful(Ok(Json.toJson(Map("" -> ""))))
  }
}
