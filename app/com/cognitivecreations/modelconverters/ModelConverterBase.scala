package com.cognitivecreations.modelconverters

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.UserMongo
import models.User

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Todd Sieland-Peteson on 1/13/15.
 */
trait ModelConverterBase[X,M] { // eXternal and Mongo
  def fromMongo(m:M): X
  def toMongo(m:X): M

  def asFuture(userInF: Future[M])(implicit executionContext: ExecutionContext): Future[X] =
    for (u <- userInF) yield fromMongo(u)
  def asFutureMongo(userInF: Future[X])(implicit executionContext: ExecutionContext): Future[M] =
    for (u <- userInF) yield toMongo(u)

  def asFutureOption(userInF: Future[Option[M]])(implicit executionContext: ExecutionContext): Future[Option[X]] =
    for (u <- userInF) yield asOption(u)
  def asFutureOptionMongo(userInF: Future[Option[X]])(implicit executionContext: ExecutionContext): Future[Option[M]] =
    for (u <- userInF) yield asOptionMongo(u)

  def asOption(userInF: Option[M]): Option[X] =
    if (userInF.isDefined) Some(fromMongo(userInF.get)) else None
  def asOptionMongo(userInF: Option[X]): Option[M] =
    if (userInF.isDefined) Some(toMongo(userInF.get)) else None


}
