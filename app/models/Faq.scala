package models

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}

/**
 * Created by Todd Sieland-Peteson on 5/25/15.
 */

case class Faq (faqId:Option[UUID], orderingIndex: Int, title:String, richText: String,
                author: Option[String], tags: Option[List[String]], vote: Option[Int],
                create: Option[DateTime], lastUpdate: Option[DateTime],
                showUntil: Option[DateTime], showFrom: Option[DateTime])

object Faq {
  implicit val faq_Writes: Writes[Faq] = (
    (JsPath \  "faqId").write[Option[UUID]] and
      (JsPath \ "orderingIndex").write[Int] and
      (JsPath \ "title").write[String] and
      (JsPath \ "richText").write[String] and
      (JsPath \ "author").write[Option[String]] and
      (JsPath \ "tags").write[Option[List[String]]] and
      (JsPath \ "vote").write[Option[Int]] and
      (JsPath \ "create").write[Option[DateTime]] and
      (JsPath \ "lastUpdate").write[Option[DateTime]] and
      (JsPath \ "showUntil").write[Option[DateTime]] and
      (JsPath \ "showFrom").write[Option[DateTime]]
    )(unlift(Faq.unapply))

  implicit val faq_Reads: Reads[Faq] = (
    (JsPath \ "faqId").read[Option[UUID]] and
      (JsPath \ "orderingIndex").read[Int] and
      (JsPath \ "title").read[String] and
      (JsPath \ "richText").read[String] and
      (JsPath \ "author").read[Option[String]] and
      (JsPath \ "tags").read[Option[List[String]]] and
      (JsPath \ "vote").read[Option[Int]] and
      (JsPath \ "create").read[Option[DateTime]] and
      (JsPath \ "lastUpdate").read[Option[DateTime]] and
      (JsPath \ "showUntil").read[Option[DateTime]] and
      (JsPath \ "showFrom").read[Option[DateTime]]
    ) (Faq.apply _)

}
