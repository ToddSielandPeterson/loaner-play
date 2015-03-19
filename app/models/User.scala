package models

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json.Json._
import play.modules.reactivemongo.json.BSONFormats._
import play.api.libs.functional.syntax._
import reactivemongo.bson.{BSONDocumentReader, BSONObjectID, BSONDocument, BSONDocumentWriter}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._



case class User(userId: Option[UUID],   // uuid for user (unique)
                firstName: String,
                lastName: String,
                email: String,
                password: Option[String],
                passwordAgain: Option[String] = None,
                address: Address,
                admin: Option[Boolean] = None)

object User {
  val defaultUser = newBlankUser()
  def newBlankUser(): User = new User(None, "", "", "", None, None, Address.newBlankAddress, None)

  implicit val user_Writes: Writes[User] = (
    (JsPath \ "userId").write[Option[UUID]] and
      (JsPath \  "firstName").write[String] and
      (JsPath \ "lastName").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "password").write[Option[String]] and
      (JsPath \ "passwordAgain").write[Option[String]] and
      (JsPath \ "address").write[Address] and
      (JsPath \ "admin").write[Option[Boolean]]
    )(unlift(User.unapply))

  implicit val user_Reads: Reads[User] = (
    (JsPath \ "userId").read[Option[UUID]] and
      (JsPath \  "firstName").read[String](minLength[String](2)) and
      (JsPath \ "lastName").read[String](minLength[String](2)) and
      (JsPath \ "email").read[String](email) and
      (JsPath \ "password").read[Option[String]] and
      (JsPath \ "passwordAgain").read[Option[String]] and
      (JsPath \ "address").read[Address] and
      (JsPath \ "admin").read[Option[Boolean]]
    ) (User.apply _)

}