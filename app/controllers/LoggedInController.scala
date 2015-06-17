package controllers

import java.util.UUID

import com.cognitivecreations.utils.SessionUtils
import models.User
import org.joda.time.DateTime
import play.api.data.Form
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Todd Sieland-Peteson on 6/1/15.
 */
trait LoggedInController {
}

class FormErrorException(value: JsValue) extends Exception
class NotLoggedInException(userId: Option[UUID]) extends Exception
class NotLoggedInAsAdminException(userId: Option[UUID]) extends NotLoggedInException(userId)

