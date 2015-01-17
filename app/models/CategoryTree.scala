package models

import java.util.UUID

import org.joda.time.DateTime

/**
 * Created by Todd Sieland-Peteson on 1/15/15.
 */

case class CategoryBranch(categoryId:UUID, name:String, ordering: Int, children:List[CategoryBranch])

case class CategoryTree(branches: List[CategoryBranch], lastUpdate: DateTime)

object CategoryBranch {
}

object CategoryTree {
}
