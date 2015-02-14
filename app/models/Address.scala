package models

import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

/**
 * Created by tsieland on 1/8/15.
 */

case class Address(addressLine1: String, 
                    addressLine2: Option[String],
                    city: String,
                    state: String, 
                    zip: String,
                    country: Option[String])

object Address {

  def newBlankAddress: Address = new Address("", None, "", "", "", None)


  implicit val address_Writes: Writes[Address] = (
      (JsPath \ "addressLine1").write[String] and
      (JsPath \ "addressLine2").write[Option[String]] and
      (JsPath \ "city").write[String] and
      (JsPath \ "state").write[String] and
      (JsPath \ "zip").write[String] and
      (JsPath \ "country").write[Option[String]]
    )(unlift(Address.unapply))

  implicit val address_Reads: Reads[Address] = (
    (JsPath \ "addressLine1").read[String] and
    (JsPath \ "addressLine2").read[Option[String]] and
    (JsPath \ "city").read[String] and
    (JsPath \ "state").read[String] and
    (JsPath \ "zip").read[String] and
    (JsPath \ "country").read[Option[String]]
  ) (Address.apply _)

}

