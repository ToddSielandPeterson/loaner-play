package com.cognitivecreations.dao.mongo.coordinator

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.CategoryDao
import com.cognitivecreations.dao.mongo.dao.mongomodel.CategoryMongo
import com.cognitivecreations.modelconverters.CategoryConverter
import models.{CategoryBranch, CategoryTree, Category}
import org.joda.time.DateTime
import reactivemongo.core.commands.LastError

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 1/13/15.
 */
class CategoryCoordinator(implicit ec: ExecutionContext) extends CategoryConverter with CoordinatorBase[Category] {
  import CategoryCoordinator._

  val categoryDao = new CategoryDao()

  def findByPrimary(uuid: UUID): Future[Option[Category]] = {
    for {
      optCat <- categoryDao.findByCategoryId(uuid.toString)
    } yield
      optCat.map(x => fromMongo(x))
  }

  def findByCategoryUniqueName(uniqueName: String): Future[Option[Category]] = {
    for {
      optCat <- categoryDao.findByCategoryUniqueName(uniqueName)
    } yield
      optCat.map(x => fromMongo(x))
  }

  def insert(category: Category): Future[LastError] = {
    findByPrimary(category.categoryId).flatMap {
      case Some(s) => failed(s"Category ${category.categoryId} already exists")
      case None => categoryDao.insert(toMongo(category))
    }
  }

  def fetchCategoryTree:CategoryTree = {
    if (updatableCategoryTree.isDefined) {
      if (updatableCategoryTree.get.lastUpdate.plusMinutes(30).isAfter(DateTime.now()))
        updateCategoryTree(buildCategoryTree)
    } else
      updateCategoryTree(buildCategoryTree)

    updatableCategoryTree.get
  }

  // build it
  def buildCategoryTree:CategoryTree = {
    def buildBranches(id: UUID, categoryList: List[CategoryMongo]): List[CategoryBranch] = {
      val eachList = categoryList.filter(cur => {cur.parentId.isDefined && cur.parentId.get == id}).
        map(cur => {
        val x = buildBranches(cur.categoryId, categoryList)
        new CategoryBranch(categoryId = cur.categoryId, name = cur.categoryName, ordering = cur.ordering, children = x)
      } )

      eachList
    }

    def buildRoot(categoryList: List[CategoryMongo]): List[CategoryBranch] = {
      val catBranchList = categoryList.filter(cur => cur.parentId.isEmpty).
        map(cur => {
        val branch = buildBranches(cur.categoryId, categoryList.filterNot(cur => cur.parentId.isEmpty))
        new CategoryBranch(categoryId = cur.categoryId, name = cur.categoryName, ordering = cur.ordering, children = branch)
      })
      catBranchList
    }

    val fCategoryList = categoryDao.findAll
    val categoryList = Await.result(fCategoryList, Duration(5, SECONDS))
    new CategoryTree(branches = buildRoot(categoryList), lastUpdate = new DateTime())
  }

}

object CategoryCoordinator {
  var updatableCategoryTree: Option[CategoryTree] = None

  def updateCategoryTree(catTree: CategoryTree) = {
    updatableCategoryTree = Some(catTree)
  }
}

