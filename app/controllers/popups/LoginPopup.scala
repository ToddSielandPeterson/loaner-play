package controllers.popups

import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

/**
  * Created by Todd Sieland-Peteson on 1/14/15.
  */
object LoginPopup extends Controller {

   def index() = Action {
//     implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

     Ok(views.html.popups.login_popup())
   }

 }
