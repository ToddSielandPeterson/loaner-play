package com.cognitivecreations.dao.mongo.dao

import java.util.UUID

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.{SessionMongo, DatabaseMongoDataSource, CategoryMongo}
import com.cognitivecreations.helpers.BSONHandlers
import reactivemongo.bson.{BSONString, BSONDocument}
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/15/15.
 */
trait SessionDaoTrait extends BaseMongoDao[SessionMongo] with BSONHandlers {
  import reactivemongo.api._
  val collection =   DatabaseMongoDataSource.sessionSource
  implicit val bsonHandler = SessionMongo.bsonHandler_SessionMongo
}

class SessionDao(implicit val executionContext: ExecutionContext) extends SessionDaoTrait {
  def bySessioKey(id: UUID): BSONDocument = byKey("sessionId", id)

  def findBySessionId(id: UUID): Future[Option[SessionMongo]] = {
    findOne(bySessioKey(id))
  }

  def upsert(id: UUID, before: SessionMongo, after: SessionMongo): Future[LastError] = {
    update(bySessioKey(before.sessionId), updateQuery(before, after), upsert = true)
  }

}

