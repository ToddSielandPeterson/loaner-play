package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.UserMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.UserMongo
import com.cognitivecreations.modelconvertors.UserConvertor._
import models.User
import org.joda.time.Seconds

import reactivemongo.core.commands.LastError
import scala.concurrent.duration._
import scala.concurrent.{duration, Await, ExecutionContext, Future}

/**
 * Created by tsieland on 11/29/14.
 */
class UserCoordinator(implicit ec: ExecutionContext) {

  val ALLOWED_FAILURE_COUNT = 5

  def optionOrString(v: Option[String], d: Option[String]): Option[String] = if (v.isDefined) v else d

  def overwrite(user: UserMongo, fName: Option[String], lName: Option[String], website: Option[String]): UserMongo = {
    user.copy(firstName = user.firstName, lastName = user.lastName,
      website = optionOrString(website, user.website))
  }

  def overwriteAsSome(user: Option[UserMongo], fName: Option[String], lName: Option[String], website: Option[String]): Option[UserMongo] = {
    if (user.isDefined) Some(overwrite(user.get, fName, lName, website)) else user
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
    import com.cognitivecreations.modelconvertors.UserConvertor._

    val userDao = new UserMongoDao()
    val foptUserMong = userDao.findByUserId(id)
    val foptUser = foptUserMong.map( optUser => optUser.map(
      userFetch => mergeUserMongo(asUserMongo(user), userFetch)
    ))
    asFutureOptionUser(foptUser)
  }

  def findByUserId(id: UUID): Future[Option[User]] = {
    val userDao = new UserMongoDao()

    try {
      for {
        user <- userDao.findByUserId(id)
      } yield user.map(u => asUser(u))
    } catch {
      case ex: Exception =>
        println(s"Exception Happened ${ex.getMessage}" )
        ex.printStackTrace
        Future.successful(None)
    }
  }

  def findAll(): Future[List[User]] = {
    val userDao = new UserMongoDao

    try {
      val y = for {
        users <- userDao.findAll
      } yield
        users.map(u => asUser(u))
      y
    } catch {
      case ex: Exception =>
        println(s"Exception Happened ${ex.getMessage}" )
        ex.printStackTrace
        Future.successful(List())
    }
  }

  def insert(user: User): Future[LastError] = {
    try {
      val userDao = new UserMongoDao()
      val y: Future[LastError] = userDao.findByUserId(user.userId.get).flatMap(optUserMongo =>
        if (optUserMongo.isDefined)
          Future.failed(new LastError(ok=false, code=None, err=Some("all"), errMsg = Some("already exists"), originalDocument = None, updated = 0, updatedExisting = false))
        else
          userDao.insert(asUserMongo(user))
      )
      y
    } catch {
      case ex: Exception =>
        println(s"Exception Happened ${ex.getMessage}" )
        ex.printStackTrace
        Future.failed(ex)
    }
  }
}
