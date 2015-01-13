package models

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json.Json._
import play.modules.reactivemongo.json.BSONFormats._
import play.api.libs.functional.syntax._
import reactivemongo.bson.{BSONDocumentReader, BSONObjectID, BSONDocument, BSONDocumentWriter}
import play.api.libs.json._
import play.api.libs.functional.syntax._



/**
 * Created by tsieland on 1/8/15.
 */
case class Category(id: UUID, name: String, parent: UUID)

object Category {
  implicit val category_Writes: Writes[Category] = (
    (JsPath \  "id").write[UUID] and
      (JsPath \ "name").write[String] and
      (JsPath \ "parent").write[UUID]
    )(unlift(Category.unapply))

  implicit val category_Reads: Reads[Category] = (
    (JsPath \ "id").read[UUID] and
      (JsPath \ "name").read[String] and
      (JsPath \ "parent").read[UUID]
    ) (Category.apply _)


}


