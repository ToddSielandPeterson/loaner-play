package com.cognitivecreations.dao.mongo.dao

import java.util.UUID

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.{PrivateMessageMongo, DatabaseMongoDataSource, ProductMongo}
import com.cognitivecreations.helpers.BSONHandlers
import org.joda.time.DateTime
import reactivemongo.bson.{BSONString, BSONDocument}
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 5/25/15.
 */

trait PrivateMessageDaoTrait extends BaseMongoDao[PrivateMessageMongo] with BSONHandlers {
  import reactivemongo.api._

  val collection =   DatabaseMongoDataSource.privateMessageSource
  implicit val bsonHandler = PrivateMessageMongo.bsonHandler_PrivateMessageMongo
}

class PrivateMessageDao(implicit val executionContext: ExecutionContext) extends PrivateMessageDaoTrait {
  def byMainKey(id: String): BSONDocument = { byKey("id", id) }

  def deleteByProductId(id: UUID): Future[LastError] = {
    delete(byMainKey(id), true)
  }

  def findByToUser(id: UUID): Future[List[PrivateMessageMongo]] = {
    find(byKey("toUser", id))
  }

  def findByFromUser(id: UUID): Future[List[PrivateMessageMongo]] = {
    find(byKey("fromUser", id))
  }

  def update(privateMessage: PrivateMessageMongo): Future[LastError] = {
    val upsertPrivateMessage = privateMessage.copy(lastUpdated = Some(new DateTime))
    update(byMainKey(privateMessage.id), update = upsertPrivateMessage, upsert = true, multi = false)
  }

  def userMessageCount(userId: Option[UUID]): Future[Int] = {
    if (userId.isDefined)
      count(byKey("toUser", userId.get))
    else {
      assert(true, "userMessageCount should have a value")
      Future(0)
    }
  }
}
