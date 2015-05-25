package models

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}

/**
 * Created by Todd Sieland-Peteson on 5/4/15.
 */
case class Image(imageId: Option[UUID],
                 owner: Option[UUID],
                 image: String,
                 thumbnail: Option[String],
                 adminOk: Boolean,
                 activeImage:Boolean,
                  lastUpdate: Option[DateTime])

object Image {
  implicit val image_Writes: Writes[Image] = (
    (JsPath \  "imageId").write[Option[UUID]] and
      (JsPath \  "owner").write[Option[UUID]] and
      (JsPath \ "image").write[String] and
      (JsPath \ "thumbnail").write[Option[String]] and
      (JsPath \ "adminOk").write[Boolean] and
      (JsPath \ "activeImage").write[Boolean] and
      (JsPath \ "lastUpdate").write[Option[DateTime]]
    )(unlift(Image.unapply))

  implicit val image_Reads: Reads[Image] = (
    (JsPath \ "imageId").read[Option[UUID]] and
      (JsPath \ "owner").read[Option[UUID]] and
      (JsPath \ "image").read[String] and
      (JsPath \ "thumbnail").read[Option[String]] and
      (JsPath \ "adminOk").read[Boolean] and
      (JsPath \ "activeImage").read[Boolean] and
      (JsPath \ "lastUpdate").read[Option[DateTime]]
    ) (Image.apply _)

}
