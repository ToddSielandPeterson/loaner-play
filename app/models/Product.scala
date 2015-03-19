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

case class Product( productId: Option[UUID] = None, // unique generated id (UUID)
                    userId: Option[UUID] = None, // link to user id

                    name: String,
                    secondLine: Option[String],
                    categoryId: Option[UUID], // link to unique category id
                    productType: Option[String] = None,

                    addedDateTime: Option[DateTime] = Some(new DateTime()),
                    lastUpdate: Option[DateTime] = Some(new DateTime()),

                    pictures: List[String] = Nil, // list of pictures
                    thumbnails: List[String] = Nil, // list of pictures
                    text: String) {
  import com.cognitivecreations.utils.SessionUtils._

  def productOwnedByUser(u: Option[User]): Boolean = {
    u.isDefined && optionUuidCompare(u.get.userId, userId)
  }

  def productOwnedByUserFromSession(userSession: Option[UserSession]): Boolean = {
    userSession.isDefined && productOwnedByUser(userSession.get.user)
  }

  def productOwnedByUserFromSession(userSession: UserSession): Boolean = {
    userId.isDefined && productOwnedByUser(userSession.user)
  }
}

object Product {

  implicit def isProductOwnedBySession(product: Option[Product], session: Option[UserSession]): Boolean = {
    product.isDefined && product.get.productOwnedByUserFromSession(session)
  }

  implicit def isProductOwnedBySession(product: Option[Product], session: UserSession): Boolean = {
    product.isDefined && product.get.productOwnedByUserFromSession(session)
  }

  def newEmptyProduct():Product = new Product(productId = None, userId = None, name = "", secondLine = None, categoryId = None, productType = None,
    addedDateTime = None, lastUpdate = None, pictures = List(), thumbnails = List(), text = "")

  implicit val product_Writes: Writes[Product] = (
      (JsPath \ "productId").write[Option[UUID]] and
      (JsPath \ "userId").write[Option[UUID]] and
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
    (JsPath \ "productId").read[Option[UUID]] and
      (JsPath \ "userId").read[Option[UUID]] and
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