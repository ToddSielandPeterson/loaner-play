package controllers.widgets

import play.api.mvc.{Action, Controller}

/**
 * Created by Todd Sieland-Peteson on 1/14/15.
 */
object Footer extends Controller {

  def index() = Action {
//    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    Ok(views.html.index())
  }

}
