package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes, Json}
import reactivemongo.bson.{BSONDocumentReader, BSONObjectID, BSONDocument, BSONDocumentWriter}
import play.modules.reactivemongo.json.BSONFormats._

/**
 * Created by tsieland on 10/14/14.
 */
case class UserPage(userId: String, header: String, text: String)

object UserPage {
  implicit val category_Writes: Writes[UserPage] = (
    (JsPath \  "userId").write[String] and
      (JsPath \ "header").write[String] and
      (JsPath \ "text").write[String]
    )(unlift(UserPage.unapply))

  implicit val category_Reads: Reads[UserPage] = (
    (JsPath \ "userId").read[String] and
      (JsPath \ "header").read[String] and
      (JsPath \ "text").read[String]
    ) (UserPage.apply _)

}