package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}

/**
 * Created by Todd Sieland-Peteson on 2/16/15.
 */

case class Error(status: Boolean, errors: List[String])


object Error {
  def apply1(status: Boolean, errors: List[String]): Error = new Error(status, errors)

  def apply(error: String): Error = new Error(false, List(error))
  def apply(error: Option[String]): Error = new Error(false, if (error.isDefined) List(error.get) else List())
  def apply(errors: List[String]): Error = new Error(false, errors)
  def success: Error = new Error(true, List())

  implicit val errors_Writes: Writes[Error] = (
    (JsPath \ "status").write[Boolean] and
      (JsPath \ "errors").write[List[String]]
    )(unlift(Error.unapply))

  implicit val errors_Reads: Reads[Error] = (
    (JsPath \ "status").read[Boolean] and
      (JsPath \ "errors").read[List[String]]
    ) (Error.apply1 _)

}

