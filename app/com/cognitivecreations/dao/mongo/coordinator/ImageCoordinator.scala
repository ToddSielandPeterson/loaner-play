package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.ImageDao
import com.cognitivecreations.modelconverters.ImageConverter
import models.Image
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 5/4/15.
 */

class ImageCoordinator(implicit ec: ExecutionContext) extends ImageConverter with CoordinatorBase[Image] {

  val imageDao = new ImageDao()

  def findByPrimary(uuid: UUID): Future[Option[Image]] = {
    for {
      optImage <- imageDao.findById(uuid.toString)
    } yield
    optImage.map(fromMongo)
  }

  def findByCategory(uuid: UUID): Future[List[Image]] = {
    for {
      imageList <- imageDao.findByUserId(uuid)
    } yield
      imageList.map(fromMongo)
  }

  def insert(image: Image): Future[LastError] = {
    imageDao.insert(toMongo(image))
  }

  def update(image: Image): Future[LastError] = {
    findByPrimary(image.imageId).flatMap {
      case None => failed(s"Product ${image.imageId.toString} does not exist")
      case Some(s) => imageDao.update(toMongo(image))
    }
  }


}
