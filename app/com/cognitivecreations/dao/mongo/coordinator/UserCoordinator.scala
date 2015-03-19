package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.UserMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.UserMongo
import com.cognitivecreations.modelconverters.UserConverter
import models.User

import reactivemongo.core.commands.LastError
import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by tsieland on 11/29/14.
 */
class UserCoordinator(implicit ec: ExecutionContext) extends UserConverter with CoordinatorBase[User]{
  val ALLOWED_FAILURE_COUNT = 5
  val userDao = new UserMongoDao()

  def optionOrString(v: Option[String], d: Option[String]): Option[String] = if (v.isDefined) v else d

  def overwrite(user: UserMongo, fName: Option[String], lName: Option[String], website: Option[String]): UserMongo = {
    user.copy(firstName = user.firstName,
      lastName = user.lastName)
  }

  def overwriteAsSome(user: Option[UserMongo], fName: Option[String], lName: Option[String], website: Option[String]): Option[UserMongo] = {
    if (user.isDefined)
      Some(overwrite(user.get, fName, lName, website))
    else
      user
  }

  def mergeUserMongo(to: UserMongo, from: UserMongo): UserMongo = {
    import UserMongo._

    updateFirstName(
      updateLastName(
        updateEmail(
          updatePassword(from, Some(to.lastName)),
          Some(to.lastName)),
        Some(to.lastName)),
      Some(to.firstName))

  }

  def mergeUserMongo(to: UserMongo, from: User): UserMongo = {
    import UserMongo._

    updateFirstName(
      updateLastName(
        updateEmail(
          updatePassword(to, Some(from.lastName)),
          Some(from.lastName)),
        Some(from.lastName)),
      Some(from.firstName))

  }

  def updateUser(user: User, id: UUID): Future[Option[User]] = {
    val foptUserMongo = findByPrimary(id)

    foptUserMongo.map( optUser =>
      optUser.map(userFetch =>
        fromMongo(mergeUserMongo(toMongo(user), userFetch))
    ))
  }

  override def findByPrimary(id: UUID): Future[Option[User]] = {
    for {
      user <- userDao.findByUserId(id)
    } yield user.map(u => fromMongo(u))
  }

  def findUserByUserName(userName: String): Future[Option[User]] = {
    for {
      user <- userDao.findUserByUserName(userName)
    } yield user.map(u => fromMongo(u))
  }

  def findAll(): Future[List[User]] = {
    for (
      users <- userDao.findAll
    ) yield
      users.map(u => fromMongo(u))
  }

  def insert(user: User): Future[LastError] = {
    findByPrimary(user.userId).flatMap {
      case Some(s) =>
        failed(s"Category ${user.userId.toString} already exists")
      case None =>
        userDao.insert(toMongo(user))
    }
  }

  def findUserByIdAndPassword(user: String, password: String):Future[Option[User]] = {
    for {
      u <- userDao.findByUserNameAndPassword(user, password)
    } yield u.map(y => fromMongo(y))
  }
}
