package com.cognitivecreations.modelconverters

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.{FaqMongo, CategoryMongo}
import com.cognitivecreations.helpers.Pimp_UUID._
import models.{Faq, Category}
import org.joda.time.DateTime

/**
 * Created by Todd Sieland-Peteson on 5/25/15.
 */

class FaqConverter extends ModelConverterBase[Faq, FaqMongo] {
  def fromMongo(faq: FaqMongo): Faq =
    Faq(faqId = Some(faq.faqId),
      orderingIndex = faq.orderingIndex,
      title = faq.title,
      richText = faq.richText,
      author = faq.author,
      tags = if (faq.tags.isEmpty) None else Some(faq.tags),
      vote = faq.vote,
      lastUpdate = Some(faq.lastUpdate),
      create = Some(faq.create),
      showUntil = Some(faq.showUntil) )

  def toMongo(faq: Faq): FaqMongo =
    FaqMongo(faqId = faq.faqId.getOrElse(UUID.randomUUID()),
      orderingIndex = faq.orderingIndex,
      title = faq.title,
      richText = faq.richText,
      author = faq.author,
      tags = faq.tags.getOrElse(List()),
      vote = faq.vote,
      lastUpdate = faq.lastUpdate.getOrElse(DateTime.now()),
      create = faq.create.getOrElse(DateTime.now()),
      showUntil = faq.showUntil.getOrElse(DateTime.now()))
}


