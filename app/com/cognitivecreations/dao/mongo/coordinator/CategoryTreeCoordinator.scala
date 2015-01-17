package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.CategoryDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.CategoryMongo
import models.{CategoryBranch, CategoryTree}
import org.joda.time.DateTime

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/17/15.
 *
 * Builds and holds a copy of the category as a tree
 */
class CategoryTreeCoordinator(categoryDao: CategoryDao)(implicit ec: ExecutionContext) {
  import CategoryTreeCoordinator._

  def fetchCategoryTree:CategoryTree = {
    updatableCategoryTree.getOrElse(buildCategoryTree)
    // check time and update as needed as future
  }

  // build it
  def buildCategoryTree:CategoryTree = {
    def buildBranches(id: UUID, categoryList: List[CategoryMongo]): List[CategoryBranch] = {
      val eachList = categoryList.filter(cur => cur.parentId.getOrElse() == id).
        map(cur => {
          val x = buildBranches(cur.categoryId, categoryList)
          new CategoryBranch(categoryId = cur.categoryId, name = cur.categoryName, ordering = cur.ordering, children = x)
        } )

      eachList
    }

    def buildRoot(categoryList: List[CategoryMongo]): List[CategoryBranch] = {
      val catBranchList = categoryList.filter(cur => cur.parentId.isEmpty).
        flatMap(x =>
        buildBranches(x.categoryId, categoryList.filterNot(cur => cur.parentId.isEmpty)))
      catBranchList
    }

    val fCategoryList = categoryDao.findAll
    val categoryList = Await.result(fCategoryList, Duration(5, SECONDS))
    new CategoryTree(branches = buildRoot(categoryList), lastUpdate = new DateTime())
  }
}

object CategoryTreeCoordinator {
  var updatableCategoryTree: Option[CategoryTree] = None
}

