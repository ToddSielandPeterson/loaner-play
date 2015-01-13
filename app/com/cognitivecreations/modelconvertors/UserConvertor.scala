package com.cognitivecreations.modelconvertors

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.UserMongo
import models.{Address, User}
import com.cognitivecreations.modelconvertors.AddressConverter._
import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Todd Sieland-Peteson on 1/11/15.
 */
object UserConvertor {
  def asFutureUser(userInF: Future[UserMongo])(implicit executionContext: ExecutionContext): Future[User] =
    for (u <- userInF) yield asUser(u)
  def asFutureUserMongo(userInF: Future[User])(implicit executionContext: ExecutionContext): Future[UserMongo] =
    for (u <- userInF) yield asUserMongo(u)

  def asFutureOptionUser(userInF: Future[Option[UserMongo]])(implicit executionContext: ExecutionContext): Future[Option[User]] =
    for (u <- userInF) yield if (u.isDefined) Some(asUser(u.get)) else None
  def asFutureOptionUserMongo(userInF: Future[Option[User]])(implicit executionContext: ExecutionContext): Future[Option[UserMongo]] =
    for (u <- userInF) yield if (u.isDefined) Some(asUserMongo(u.get)) else None

  def asOptionUser(userInF: Option[UserMongo])(implicit executionContext: ExecutionContext): Option[User] =
    if (userInF.isDefined) Some(asUser(userInF.get)) else None
  def asOptionUserMongo(userInF: Option[User])(implicit executionContext: ExecutionContext): Option[UserMongo] =
    if (userInF.isDefined) Some(asUserMongo(userInF.get)) else None

  def asUser(userIn: UserMongo): User = {
    User(
      firstName = userIn.firstName,
      lastName = userIn.lastName,
      email = userIn.email,
      userId = Some(UUID.fromString(userIn.userId)),
      password = None,
      website = userIn.website,
      address = asAddress(userIn.address.get))
  }

  def asUserWithHidden(userIn: UserMongo): User = {
    User(
      firstName = userIn.firstName,
      lastName = userIn.lastName,
      email = userIn.email,
      userId = Some(UUID.fromString(userIn.userId)),
      password = Some(userIn.password),
      website = userIn.website,
      address = asAddress(userIn.address.get))
  }

  def asUserMongo(userIn: User): UserMongo = {
    UserMongo(firstName = userIn.firstName,
      lastName = userIn.lastName,
      email = userIn.email,
      userId = userIn.userId.getOrElse(UUID.randomUUID()).toString,
      password = userIn.password.getOrElse(""),
      website = userIn.website,
      address = Some(asAddressMongo(userIn.address)))
  }

  def asUserMongoWithHidden(userIn: User): UserMongo = {
    UserMongo(firstName = userIn.firstName,
      lastName = userIn.lastName,
      email = userIn.email,
      userId = userIn.userId.getOrElse(UUID.randomUUID()).toString,
      password = userIn.password.getOrElse(""),
      website = userIn.website,
      address = Some(asAddressMongo(userIn.address)))
  }

}
