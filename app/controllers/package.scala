import java.util.UUID

import com.cognitivecreations.utils.SessionUtils
import models.User
import org.joda.time.DateTime
import play.api.data.Form
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContent, Request}
import play.api.libs.json.Json
import reactivemongo.core.commands.LastError

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 6/2/15.
 */
package object controllers {
  def stringToDateTime(s: String): DateTime = DateTime.parse(s)
  def optionDateTime(optdt: Option[String]): Option[DateTime] = optdt.map(stringToDateTime)

  def optionInt(optint: Option[String]): Option[Int] = optint.map(_.toInt)
  def optionId(optint: Option[String]): Option[UUID] = optint.map(UUID.fromString(_))

  def asJsonError(str: String): JsValue = Json.toJson(Map("error"->str))
  def errorOrUnknownAsJson(errors: LastError): JsValue = asJsonError(errors.errMsg.getOrElse("Unknown"))

  def loggedInUser()(implicit request: Request[AnyContent], simpleDbLookups: ExecutionContext): Future[User] = {
    for {
      session <- SessionUtils(request).fetchFutureSessionInfo()
    } yield {
      if (session.user.isEmpty || session.user.get.userId.isEmpty) {
        throw new NotLoggedInException(None)
      } else {
        session.user.get
      }
    }
  }
  def loggedInAdminUser()(implicit request: Request[AnyContent], simpleDbLookups: ExecutionContext): Future[User] = {
    loggedInUser.map {
      case user if user.admin.getOrElse(false) => user
      case user => throw new NotLoggedInAsAdminException(user.userId)
    }
  }

  def errorTest[T](form: Form[T]):Boolean = {
    if (form.hasErrors) throw new FormErrorException(form.errorsAsJson)
    else true
  }


}
