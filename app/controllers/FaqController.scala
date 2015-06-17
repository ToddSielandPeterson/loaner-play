package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.{FaqCoordinator, UserCoordinator}
import com.cognitivecreations.modelconverters.CategoryConverter
import com.cognitivecreations.utils.SessionUtils
import models._
import org.joda.time.DateTime
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.concurrent.Akka
import play.api.libs.json.Json
import play.api.mvc.{Request, AnyContent, Action, Controller}
import ui.Pagelet

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Try

/**
 * Created by tsieland on 1/7/15.
 */

object FaqController extends Controller with LoggedInController {
  import models.Faq._

  def faqList() = Action.async{ implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val faqCoordinator = new FaqCoordinator()

    {
      for {
        user <- loggedInUser
        faqList <- faqCoordinator.findAllActive()
      } yield
        Ok(Json.toJson(faqList))
    }.recover{
      case ex: NotLoggedInException => Unauthorized
    }
  }

  def faq(id: String) = Action.async{ implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val faqCoordinator = new FaqCoordinator()

    {
      for {
        user <- loggedInUser
        faqList <- faqCoordinator.findByPrimary(UUID.fromString(id))
      } yield
      Ok(Json.toJson(faqList))
    }.recover{
      case ex: NotLoggedInException => Unauthorized
      case ex: IllegalArgumentException => BadRequest // from faq id being wrong
    }
  }

  def faqUpdate(faqStringId: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val faqCoordinator = new FaqCoordinator()
    val faqFormInputHolder = FaqForm.bindFromRequest()

    {
      errorTest(faqFormInputHolder)
      for {
        user <- loggedInAdminUser()
        faqData = faqFormInputHolder.get // if there are errors it will throw an exception
        faq = fromFaqData(faqData)
        errors <- faqCoordinator.update(fromFaqData(faqData))
      } yield {
        if (errors.inError)
          ExpectationFailed(errorOrUnknownAsJson(errors))
        else
          Ok (Json.toJson(faq))
      }
    }.recover{
      case ex: FormErrorException => ExpectationFailed(ex.getMessage)
      case ex: NotLoggedInAsAdminException => Unauthorized
      case ex: IllegalArgumentException => BadRequest // from faq id being wrong
    }
  }

  def faqDelete(faqStringId: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val faqCoordinator = new FaqCoordinator()

    {
      val faqId = UUID.fromString(faqStringId)
      for {
        user <- loggedInAdminUser()
        productShow <- faqCoordinator.delete(faqId)
      } yield {
        Ok(Json.toJson(Error.success))
      }
    }.recover{
      case ex: NotLoggedInAsAdminException => Unauthorized
      case ex: IllegalArgumentException => BadRequest // from faq id being wrong
    }
  }

  def addFaq() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val faqCoordinator = new FaqCoordinator()
    val faqFormInputHolder = FaqForm.bindFromRequest()

    {
      errorTest(faqFormInputHolder)
      for {
        user <- loggedInAdminUser()
        faqData = faqFormInputHolder.get // if there are errors it will throw an exception
        faq = fromFaqData (faqData)
        errors <- faqCoordinator.insert (fromFaqData (faqData) )
      } yield {
        if (errors.inError)
          ExpectationFailed(errorOrUnknownAsJson(errors))
        else
          Ok (Json.toJson(faq))
      }
    }.recover{
      case ex: FormErrorException => ExpectationFailed(ex.getMessage)
      case ex: NotLoggedInAsAdminException => Unauthorized
      case ex: IllegalArgumentException => BadRequest // from faq id being wrong
    }
  }

  case class FaqData(faqId:Option[String], orderingIndex: Int, title:String, richText: String,
                     author: Option[String],
                     //tags: Option[List[String]],
                     vote: Option[Int],
                     create: Option[String], lastUpdate: Option[String],
                     showUntil: Option[String], showFrom: Option[String])

  def fromFaqData(faq: FaqData): Faq = {
    new Faq(
      faqId = optionId(faq.faqId),
      orderingIndex = faq.orderingIndex.toInt,
      title = faq.title,
      richText = faq.richText,
      author = faq.author,
      tags = None,
      vote = faq.vote,
      lastUpdate = Some(DateTime.now()),
      create = None,
      showUntil = optionDateTime(faq.showUntil),
      showFrom = optionDateTime(faq.showFrom))
  }
  def updateFaqData(faq: Faq, faqFormData: FaqData): Faq = {
    faq.copy(
      faqId = optionId(faqFormData.faqId),
      orderingIndex = faqFormData.orderingIndex.toInt,
      title = faqFormData.title,
      richText = faqFormData.richText,
      author = faqFormData.author,
      vote = faqFormData.vote,
      lastUpdate = Some(DateTime.now()),
      showUntil = optionDateTime(faqFormData.showUntil),
      showFrom = optionDateTime(faqFormData.showFrom))
  }
  def newFaqData(faq: FaqData): Faq = {
    new Faq(
      faqId = Some(UUID.randomUUID()),
      orderingIndex = faq.orderingIndex.toInt,
      title = faq.title,
      richText = faq.richText,
      author = faq.author,
      tags = None,
      vote = Some(0),
      lastUpdate = Some(DateTime.now()),
      create = Some(DateTime.now()),
      showUntil = optionDateTime(faq.showUntil),
      showFrom = optionDateTime(faq.showFrom))
  }

  val FaqForm = Form(
    mapping(
      "faqId" -> optional(text),
      "orderingIndex" -> number,
      "title" -> text,
      "richText" -> text,
      "author" -> optional(text),
//      "tags" -> optional(List(text)),
      "vote" -> optional(number),
      "create" -> optional(text),
      "lastUpdate" -> optional(text),
      "showUntil" -> optional(text),
      "showFrom" -> optional(text)
    )(FaqData.apply)(FaqData.unapply)
  )

  val FaqInsertForm = Form(
    mapping(
      "faqId" -> optional(text),
      "orderingIndex" -> number,
      "title" -> text,
      "richText" -> text,
      "author" -> optional(text),
      "vote" -> optional(number),
      "create" -> optional(text),
      "lastUpdate" -> optional(text),
      "showUntil" -> optional(text),
      "showFrom" -> optional(text)
    )(FaqData.apply)(FaqData.unapply)
  )

}
