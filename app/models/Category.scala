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

case class Category(categoryId: UUID, name: String, uniqueName: String, ordering: Int, parentId: Option[UUID])

object Category {
  implicit val category_Writes: Writes[Category] = (
    (JsPath \  "categoryId").write[UUID] and
      (JsPath \ "name").write[String] and
      (JsPath \ "uniqueName").write[String] and
      (JsPath \ "ordering").write[Int] and
      (JsPath \ "parentId").write[Option[UUID]]
    )(unlift(Category.unapply))

  implicit val category_Reads: Reads[Category] = (
    (JsPath \ "categoryId").read[UUID] and
      (JsPath \ "name").read[String] and
      (JsPath \ "uniqueName").read[String] and   // only lower case chars, digits and _
      (JsPath \ "ordering").read[Int] and
      (JsPath \ "parentId").read[Option[UUID]]
    ) (Category.apply _)


}


