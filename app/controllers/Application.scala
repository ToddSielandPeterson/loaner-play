package controllers

import java.io.File
import java.util.UUID

import controllers.LoadDataController._
import play.Play
import play.api.libs.iteratee.Enumerator
import play.api.mvc.{Cookie, Action, Controller}

/*
 * Author: Sari Haj Hussein
 */

object Application extends Controller {
  
  /** serve the index page app/views/index.scala.html */
  def index(any: String) = Action { request =>
    val optCookieId = request.cookies.get("sessioninfo")
    val sessionId = optCookieId.getOrElse(UUID.randomUUID())

    if (optCookieId.isDefined)
      Ok(views.html.index())
    else
      Ok(views.html.index()).withCookies(Cookie("sessioninfo", sessionId.toString, Some(86400 * 31)))
  }
  
}