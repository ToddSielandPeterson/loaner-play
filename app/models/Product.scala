package models

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json.Json._
import play.modules.reactivemongo.json.BSONFormats._
import play.api.libs.functional.syntax._
import reactivemongo.bson.{BSONDocumentReader, BSONObjectID, BSONDocument, BSONDocumentWriter}
import com.cognitivecreations.helpers.BSONHandlers._
import play.api.libs.json._
import play.api.libs.functional.syntax._


/**
 * Created by Todd Sieland-Peterson on 10/14/14.
 */
case class Product( id: Option[String], // unique generated id (UUID)
                    user: String, // link to user id

                    name: String,
                    secondLine: Option[String],
                    categoryId: Option[UUID], // link to unique category id
                    productType: Option[String] = None,

                    addedDateTime: Option[DateTime],
                    lastUpdate: Option[DateTime],

                    pictures: List[String], // list of pictures
                    thumbnails: List[String], // list of pictures
                    text: String)

object Product {

  implicit val product_Writes:Writes[Product] = (
      (JsPath \ "id").write[Option[String]] and
      (JsPath \ "user").write[String] and
      (JsPath \ "name").write[String] and
      (JsPath \ "secondLine").write[Option[String]] and
      (JsPath \ "categoryId").write[Option[UUID]] and
      (JsPath \ "productType").write[Option[String]] and
      (JsPath \ "addedDateTime").write[Option[DateTime]] and
      (JsPath \ "lastUpdate").write[Option[DateTime]] and
      (JsPath \ "pictures").write[List[String]] and
      (JsPath \ "thumbnails").write[List[String]] and
      (JsPath \ "text").write[String]
    )(unlift(Product.unapply))

  implicit val product_Reads: Reads[Product] = (
    (JsPath \ "id").read[Option[String]] and
      (JsPath \ "user").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "secondLine").read[Option[String]] and
      (JsPath \ "categoryId").read[Option[UUID]] and
      (JsPath \ "productType").read[Option[String]] and
      (JsPath \ "addedDateTime").read[Option[DateTime]] and
      (JsPath \ "lastUpdate").read[Option[DateTime]] and
      (JsPath \ "pictures").lazyRead(Reads.list[String]) and
      (JsPath \ "thumbnails").lazyRead(Reads.list[String]) and
      (JsPath \ "text").read[String]
    )(Product.apply _)

}