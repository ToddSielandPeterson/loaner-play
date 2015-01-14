package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import reactivemongo.core.commands.LastError

import scala.concurrent.Future

/**
 * Created by Todd Sieland-Peteson on 1/13/15.
 */

trait CoordinatorBase[X] {

//  def xToM(x: X): M
//  def mToX(m: M): X

  def failed(errorMessage: String): Future[LastError] =
    Future.failed(new LastError(ok = false, code = None, err = Some("all"),
      errMsg = Some(errorMessage), originalDocument = None, updated = 0, updatedExisting = false))

  def findByPrimary(uuid: UUID): Future[Option[X]]

  def findByPrimary(uuid: Option[UUID]): Future[Option[X]] = {
    uuid match {
      case None => Future.successful(None)
      case Some(s) => findByPrimary(s)
    }
  }

}
