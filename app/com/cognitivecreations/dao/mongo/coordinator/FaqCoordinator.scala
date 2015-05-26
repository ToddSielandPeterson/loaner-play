package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.FaqDao
import com.cognitivecreations.modelconverters.FaqConverter
import models.Faq
import org.joda.time.DateTime

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 5/25/15.
 */
class FaqCoordinator(implicit ec: ExecutionContext) extends CoordinatorBase[Faq]{
  val ALLOWED_FAILURE_COUNT = 5
  val faqDao = new FaqDao
  val faqConverter = new FaqConverter

  def findByPrimary(uuid: UUID): Future[Option[Faq]] = {
    for {
      optCat <- faqDao.findByFaqId(uuid)
    } yield optCat.map(faqConverter.fromMongo)
  }


}
