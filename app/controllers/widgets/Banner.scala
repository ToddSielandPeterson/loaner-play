package controllers.widgets

import com.cognitivecreations.dao.mongo.coordinator.CategoryTreeCoordinator
import com.cognitivecreations.dao.mongo.dao.CategoryDao
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}
import play.api.Play.current

import scala.concurrent.ExecutionContext

/**
 * Created by Todd Sieland-Peteson on 1/14/15.
 */

object Banner extends Controller {

  def index(embed: Boolean = false) = Action {
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val bannerLink = "http://www.example.com/banner.jpg"
    val categoryDao = new CategoryDao()
    val catTreeDao = new CategoryTreeCoordinator(categoryDao)

    val catTree = catTreeDao.fetchCategoryTree

    if (embed)
      Ok(views.html.widgets.banner(bannerLink, catTree))
    else
      Ok(views.html.widgets.banner_full(bannerLink, catTree))
  }

}
