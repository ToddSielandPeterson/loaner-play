package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.UserReviewsDao
import com.cognitivecreations.modelconverters.UserReviewConverter
import models.{UserReviews, Image}
import reactivemongo.core.commands.LastError

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Todd Sieland-Peteson on 5/4/15.
 */

class UserReviewCoordinator(implicit ec: ExecutionContext) extends UserReviewConverter with CoordinatorBase[UserReviews] {

  val userReviewsDao = new UserReviewsDao()

  def findByPrimary(uuid: UUID): Future[Option[UserReviews]] = {
    for {
      optReview <- userReviewsDao.findById(uuid.toString)
    } yield
    optReview.map(fromMongo)
  }

  def findByOwner(uuid:UUID): Future[List[UserReviews]] = {
    for {
      reviews <- userReviewsDao.findByOwner(uuid)
    } yield
      reviews.map(fromMongo)
  }

  def findByReviewer(uuid:UUID): Future[List[UserReviews]] = {
    for {
      reviews <- userReviewsDao.findByReviewer(uuid)
    } yield
      reviews.map(fromMongo)
  }

  def findByProduct(uuid:UUID): Future[List[UserReviews]] = {
    for {
      reviews <- userReviewsDao.findByProduct(uuid)
    } yield
      reviews.map(fromMongo)
  }

  def insert(userReviews: UserReviews): Future[LastError] = {
    userReviewsDao.insert(toMongo(userReviews))
  }

  def update(userReviews: UserReviews): Future[LastError] = {
    findByPrimary(userReviews.id).flatMap {
      case None => failed(s"Product ${userReviews.id.toString} does not exist")
      case Some(s) => userReviewsDao.update(toMongo(userReviews))
    }
  }


}
