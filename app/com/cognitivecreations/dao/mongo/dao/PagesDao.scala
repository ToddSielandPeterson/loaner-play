package com.cognitivecreations.dao.mongo.dao

import java.util.UUID

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel._
import com.cognitivecreations.helpers.BSONHandlers
import org.joda.time.DateTime
import reactivemongo.bson.{BSONString, BSONDocument}
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

trait PagesDaoTrait extends BaseMongoDao[PagesMongo] with BSONHandlers {
  import reactivemongo.api._
  val collection =   DatabaseMongoDataSource.pagesSource
  implicit val bsonHandler = PagesMongo.bsonHandler_PagesMongo
}


class PagesDao(implicit val executionContext: ExecutionContext) extends PagesDaoTrait {
  def byUserKey(id: String): BSONDocument = { byKey("userPageId", id) }
  def byMainKey(id: String): BSONDocument = { byKey("pageId", id) }

  def deleteByProductId(id: UUID): Future[LastError] = {
    delete(byMainKey(id), true)
  }

  def findByUserId(id: String): Future[Option[PagesMongo]] = { findOne(byUserKey(id)) }
  def findByPageId(id: String): Future[Option[PagesMongo]] = { findOne(byUserKey(id)) }

  def update(pages: PagesMongo): Future[LastError] = {
    val upsertPrivateMessage = pages.copy(lastUpdated = Some(new DateTime))
    update(byMainKey(pages.userPageId), update = upsertPrivateMessage, upsert = true, multi = false)
  }

  def userPageCount(userId: Option[UUID]): Future[Int] = {
    if (userId.isDefined)
      count(byKey("toUser", userId.get))
    else {
      assert(true, "userMessageCount should have a value")
      Future(0)
    }
  }
}

