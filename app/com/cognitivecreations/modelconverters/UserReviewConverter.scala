package com.cognitivecreations.modelconverters

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.UserReviewsMongo
import models.UserReviews
import org.joda.time.DateTime

/**
 * Created by Todd Sieland-Peteson on 5/5/15.
 */
class UserReviewConverter extends ModelConverterBase[UserReviews, UserReviewsMongo] {
  def fromMongo(image: UserReviewsMongo): UserReviews = {
    UserReviews(
      id = Some(image.id),
      reviewer = Some(image.reviewer),
      owner = Some(image.owner),
      product = image.product,
      lastUpdate = Some(image.lastUpdate),
      created = Some(image.created),
      rating = Some(image.rating),
      title = Some(image.bodyText),
      bodyText = Some(image.bodyText),
      flagged = image.flagged,
      flagCount = image.flagCount,
      weight = Some(image.weight)
    )
  }

  def toMongo(image: UserReviews): UserReviewsMongo = {
    UserReviewsMongo(
      id = image.id.getOrElse(UUID.randomUUID()),
      reviewer = image.reviewer.get,
      owner = image.owner.get,
      product = image.product,
      lastUpdate = image.lastUpdate.getOrElse(DateTime.now()),
      created = image.created.getOrElse(DateTime.now()),
      rating = image.rating.getOrElse(0),
      title = image.title.getOrElse(""),
      bodyText = image.bodyText.getOrElse(""),
      flagged = image.flagged,
      flagCount = image.flagCount,
      weight = image.weight.getOrElse(0)   )
  }
}