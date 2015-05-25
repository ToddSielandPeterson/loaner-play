package models

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
 * Created by Todd Sieland-Peteson on 5/25/15.
 */

case class PrivateMessage(id: Option[UUID] = None,
                     fromUser: Option[UUID] = None,
                     toUser: Option[UUID],
                     subject: String,
                     body: String,
                     attachments: List[String] = List(),
                     newMessage: Option[Boolean],
                     read: Option[Boolean],
                     replyId: Option[UUID], // uuid of Private Message back
                     created: Option[DateTime],
                     updated: Option[DateTime],
                     opened: Option[DateTime]
                           ) {
  import com.cognitivecreations.utils.SessionUtils._

//  def productOwnedByUser(u: Option[User]): Boolean = {
//    if (u.isDefined && u.get.userId.isDefined)
//      u.get.userId.get == toUser
//    else
//      false
//  }
//
//  def productOwnedByUserFromSession(userSession: Option[UserSession]): Boolean = {
//    userSession.isDefined && productOwnedByUser(userSession.get.user)
//  }
//
//  def productOwnedByUserFromSession(userSession: UserSession): Boolean = {
//    productOwnedByUser(userSession.user)
//  }
}

object PrivateMessage {
//  def apply(id: Option[UUID], fromUser: Option[UUID], toUser: Option[UUID], subject: String, body: String) =
//    new PrivateMessage(id, fromUser, toUser, subject, body)

//  implicit def isProductOwnedBySession(product: Option[PrivateMessage], session: Option[UserSession]): Boolean = {
//    product.isDefined && product.get.productOwnedByUserFromSession(session)
//  }
//
//  implicit def isProductOwnedBySession(product: Option[PrivateMessage], session: UserSession): Boolean = {
//    product.isDefined && product.get.productOwnedByUserFromSession(session)
//  }

  implicit val privateMessage_Writes: Writes[PrivateMessage] = (
    (JsPath \ "id").write[Option[UUID]] and
      (JsPath \ "fromUser").write[Option[UUID]] and
      (JsPath \ "toUser").write[Option[UUID]] and
      (JsPath \ "subject").write[String] and
      (JsPath \ "body").write[String] and
      (JsPath \ "attachments").write[List[String]] and
      (JsPath \ "newMessage").write[Option[Boolean]] and
      (JsPath \ "read").write[Option[Boolean]] and
      (JsPath \ "replyId").write[Option[UUID]] and
      (JsPath \ "created").write[Option[DateTime]] and
      (JsPath \ "updated").write[Option[DateTime]] and
      (JsPath \ "opened").write[Option[DateTime]]
    )(unlift(PrivateMessage.unapply))

  implicit val privateMessage_Reads: Reads[PrivateMessage] = (
    (JsPath \ "id").read[Option[UUID]] and
      (JsPath \ "fromUser").read[Option[UUID]] and
      (JsPath \ "toUser").read[Option[UUID]] and
      (JsPath \ "subject").read[String] and
      (JsPath \ "body").read[String] and
      (JsPath \ "attachments").read[List[String]] and
      (JsPath \ "newMessage").read[Option[Boolean]] and
      (JsPath \ "read").read[Option[Boolean]] and
      (JsPath \ "replyId").read[Option[UUID]] and
      (JsPath \ "created").read[Option[DateTime]] and
      (JsPath \ "updated").read[Option[DateTime]] and
      (JsPath \ "opened").read[Option[DateTime]]
    )(PrivateMessage.apply _)
}