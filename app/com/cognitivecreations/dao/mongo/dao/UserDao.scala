package com.cognitivecreations.dao.mongo.dao

import java.util.UUID

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.{UserMongo, DatabaseMongoDataSource}
import com.cognitivecreations.helpers.BSONHandlers
import models.User
import reactivemongo.bson.{BSONString, BSONDocument}

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/11/15.
 */

trait UserMongoDaoTrait extends BaseMongoDao[UserMongo] with BSONHandlers {
  import reactivemongo.api._
  val collection =   DatabaseMongoDataSource.userDataSource
  implicit val bsonHandler = UserMongo.bsonHandler_UserMongoProperty
}

class UserMongoDao(implicit val executionContext: ExecutionContext) extends UserMongoDaoTrait {
  import UserMongo._

  def byUserKey(id:UUID): BSONDocument = byKey("userId", id)

  def addUser(user: UserMongo): Future[Option[UserMongo]] = {
    for (
      error <- insert(user)
    ) yield {
      if (error.ok) Some(user)
      else None
    }
  }

  def findByUserId(id: UUID): Future[Option[UserMongo]] = { findOne(byUserKey(id)) }

  def findByUserId(id: Option[UUID]): Future[Option[UserMongo]] = {
    if (id.isDefined) findByUserId(id.get)
    else Future.successful(None)
  }

  def findUserByUserName(userName: String): Future[Option[UserMongo]] = {
    findOne(BSONDocument("email" -> BSONString(userName)))
  }

  def findByUserNameAndPassword(userName: String, password: String): Future[Option[UserMongo]] = {
    for {
      u <- findUserByUserName(userName)
    } yield {
      if (u.isDefined && u.get.password.equals(password)) u
      else None
    }
  }
}


