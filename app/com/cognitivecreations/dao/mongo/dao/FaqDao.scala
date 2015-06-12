package com.cognitivecreations.dao.mongo.dao

import java.util.UUID

import com.cognitivecreations.dao.mongo.BaseMongoDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.{FaqMongo, DatabaseMongoDataSource}
import com.cognitivecreations.helpers.BSONHandlers
import org.joda.time.DateTime
import reactivemongo.bson.BSONDocument
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 5/25/15.
 */

trait FaqDaoTrait extends BaseMongoDao[FaqMongo] with BSONHandlers {
  import reactivemongo.api._

  val collection =   DatabaseMongoDataSource.faq
  implicit val bsonHandler = FaqMongo.bsonHandler_FaqMongo
}


class FaqDao(implicit val executionContext: ExecutionContext) extends FaqDaoTrait {

  def byMainKey(id: String): BSONDocument = { byKey("faqId", id) }

  def findByUrl(url: String): Future[Option[FaqMongo]] = { findOne(byKey("image", url)) }

  def findByFaqId(id: UUID): Future[Option[FaqMongo]] = { findOne(byMainKey(id)) }

  def delete(uuid: UUID): Future[LastError] = {
    delete(byMainKey(uuid), true)
  }

  def update(faq: FaqMongo): Future[LastError] = {
    val upsertProduct = faq.copy(lastUpdate = DateTime.now())
    update(byMainKey(faq.faqId), update = upsertProduct, upsert = true, multi = false)
  }
}
