package models

import java.util.UUID

import org.joda.time.DateTime
import play.api.libs.json.{JsPath, Writes}
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

/**
 * Created by Todd Sieland-Peteson on 1/15/15.
 */

case class CategoryBranch(categoryId:UUID, name:String, uniqueName: String, ordering: Int, children:List[CategoryBranch]) {
  def hasBranches: Boolean = children.size > 0
}

case class CategoryTree(branches: List[CategoryBranch], lastUpdate: DateTime) {
  def hasBranches: Boolean = branches.size > 0
}

object CategoryBranch {
}

object CategoryTree {
}

case class FlatCategory(categoryId: String, name: String)

object FlatCategory {
  implicit val CategoryFlat_Writes: Writes[FlatCategory] = (
    (JsPath \ "categoryId").write[String] and
      (JsPath \ "categoryPath").write[String]
    )(unlift(FlatCategory.unapply))
}