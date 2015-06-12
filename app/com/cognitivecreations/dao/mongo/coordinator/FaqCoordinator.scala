package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.FaqDao
import com.cognitivecreations.modelconverters.FaqConverter
import models.{Image, Faq}
import org.joda.time.DateTime
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 5/25/15.
 */

class FaqCoordinator(implicit ec: ExecutionContext) extends CoordinatorBase[Faq] {
  val ALLOWED_FAILURE_COUNT = 5
  val faqDao = new FaqDao()

  def findByPrimary(uuid: UUID): Future[Option[Faq]] = {
    for {
      optCat <- faqDao.findByFaqId(uuid)
    } yield optCat.map(FaqConverter.fromMongo)
  }

  def findAllActive(): Future[List[Faq]] = {
    for (
      list <- faqDao.findAll
    ) yield
      list.filter(x => x.showUntil.compareTo(DateTime.now()) >= 0 && x.showFrom.compareTo(DateTime.now()) <= 0)
        .map(FaqConverter.fromMongo)
        .sortWith((x,y) => x.orderingIndex < y.orderingIndex)
  }

  def findAll(): Future[List[Faq]] = {
    for (
      list <- faqDao.findAll
    ) yield
      list.map(FaqConverter.fromMongo)
        .sortWith((x,y) => x.orderingIndex < y.orderingIndex)
  }

  def delete(uuid: UUID): Future[LastError] = {
    faqDao.delete(uuid)
  }

  def insert(faq: Faq): Future[LastError] = {
    faqDao.insert(FaqConverter.toMongo(faq))
  }

  def update(faq: Faq): Future[LastError] = {
    findByPrimary(faq.faqId).flatMap {
      case None => failed(s"faq ${faq.faqId.toString} does not exist")
      case Some(s) => faqDao.update(FaqConverter.toMongo(faq))
    }
  }

}
