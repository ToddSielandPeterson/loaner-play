package com.cognitivecreations.utils

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.cognitivecreations.dao.mongo.coordinator.SessionCoordinator
import models.UserSession
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.duration.Duration
import scala.concurrent.{Future, Await, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/18/15.
 */

class SessionUtils(request: Request[AnyContent])(implicit ec: ExecutionContext) {

  val optCookieId = request.cookies.get("sessioninfo")
  val sessionCoordinator = new SessionCoordinator()

  def fetchAllSessionInfo(): UserSession = {
    optCookieId.map( x => {
      val optSession = Await.result(sessionCoordinator.findByPrimary(UUID.fromString(x.value)), Duration(5, TimeUnit.SECONDS))
      optSession.getOrElse(sessionCoordinator.insertNewSession(UUID.randomUUID()))
    }
    ).getOrElse(sessionCoordinator.insertNewSession(UUID.randomUUID()))
  }

  def fetchFutureSessionInfo(): Future[UserSession] = {
    if (optCookieId.isDefined)
      for {
        optSession <- sessionCoordinator.findByPrimary(UUID.fromString(optCookieId.get.value))
      } yield
        optSession.getOrElse(sessionCoordinator.insertNewSession(UUID.randomUUID()))
    else
      Future.successful(sessionCoordinator.insertNewSession(UUID.randomUUID()))
  }


}
