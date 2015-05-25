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
  def byKey(key: String, value: String): BSONDocument = {
    BSONDocument(key -> BSONString(value))
  }
  def byMainKey(id: String): BSONDocument = {
    BSONDocument("id" -> BSONString(id))
  }
  def byMainKey(id: UUID): BSONDocument = {
    BSONDocument("id" -> BSONString(id.toString))
  }

  def deleteByProductId(id: UUID): Future[LastError] = {
    delete(byMainKey(id), true)
  }

  def findByToUser(id: UUID): Future[List[PrivateMessageMongo]] = {
    find(byKey("toUser", id.toString))
  }

  def findByFromUser(id: UUID): Future[List[PrivateMessageMongo]] = {
    find(byKey("fromUser", id.toString))
  }

  def update(privateMessage: PrivateMessageMongo): Future[LastError] = {
    val upsertPrivateMessage = privateMessage.copy(lastUpdated = Some(new DateTime))
    update(byMainKey(privateMessage.id), update = upsertPrivateMessage, upsert = true, multi = false)
  }
}
