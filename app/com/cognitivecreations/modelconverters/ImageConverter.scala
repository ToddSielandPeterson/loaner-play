package com.cognitivecreations.modelconverters

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.ImageMongo
import models.Image

/**
 * Created by Todd Sieland-Peteson on 5/5/15.
 */
class ImageConverter extends ModelConverterBase[Image, ImageMongo] {
  def fromMongo(image: ImageMongo): Image = {
    Image(imageId = image.imageId,
      owner = image.owner,
      image = image.image,
      thumbnail = image.thumbnail,
      adminOk = image.adminOk,
      activeImage = image.activeImage,
      lastUpdate = image.lastUpdate
    )
  }

  def toMongo(image: Image): ImageMongo = {
    ImageMongo(imageId = image.imageId,
      owner = image.owner,
      image = image.image,
      thumbnail = image.thumbnail,
      adminOk = image.adminOk,
      activeImage = image.activeImage,
      lastUpdate = image.lastUpdate
    )
  }
}