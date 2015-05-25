package models

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}

/**
 * Created by Todd Sieland-Peteson on 5/6/15.
 */
case class UserReviews(id: Option[UUID],
                       reviewer: Option[UUID],
                       owner: Option[UUID],
                       product: Option[UUID],

                       rating: Option[Int], // 0-5
                       title: Option[String],
                       bodyText: Option[String],
                       flagged: Option[Boolean],
                       flagCount: Option[Int],
                       weight: Option[Int], // higher weight bubble to top
                       lastUpdate: Option[DateTime],
                        created: Option[DateTime]
                       ) {
  def productReview: Boolean = product.isDefined
  def ownerReveiw: Boolean = !productReview
  // add sort method
}

object UserReviews{
//  def defaultUserSession():UserReviews = new UserReviews(UUID.randomUUID())
  implicit val userReviews_Writes: Writes[UserReviews] = (
    (JsPath \ "id").write[Option[UUID]] and
      (JsPath \ "reviewer").write[Option[UUID]] and
      (JsPath \ "owner").write[Option[UUID]] and
      (JsPath \ "product").write[Option[UUID]] and
      (JsPath \ "rating").write[Option[Int]] and
      (JsPath \ "title").write[Option[String]] and
      (JsPath \ "bodyText").write[Option[String]] and
      (JsPath \ "flagged").write[Option[Boolean]] and
      (JsPath \ "flagCount").write[Option[Int]] and
      (JsPath \ "weight").write[Option[Int]] and
      (JsPath \ "lastUpdate").write[Option[DateTime]] and
      (JsPath \ "created").write[Option[DateTime]]
    )(unlift(UserReviews.unapply))

  implicit val userReviews_Reads: Reads[UserReviews] = (
    (JsPath \ "id").read[Option[UUID]] and
      (JsPath \ "reviewer").read[Option[UUID]] and
      (JsPath \ "owner").read[Option[UUID]] and
      (JsPath \ "product").read[Option[UUID]] and
      (JsPath \ "rating").read[Option[Int]] and
      (JsPath \ "title").read[Option[String]] and
      (JsPath \ "bodyText").read[Option[String]] and
      (JsPath \ "flagged").read[Option[Boolean]] and
      (JsPath \ "flagCount").read[Option[Int]] and
      (JsPath \ "weight").read[Option[Int]] and
      (JsPath \ "lastUpdate").read[Option[DateTime]] and
      (JsPath \ "created").read[Option[DateTime]]
    )(UserReviews.apply _)

}

