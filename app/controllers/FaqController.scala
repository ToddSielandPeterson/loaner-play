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

    for {
      user <- loggedInUser
      faqList <- faqCoordinator.findAllActive()
    } yield
      Ok(Json.toJson(faqList))
  }

  def productAddForUser() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val faqCoordinator = new FaqCoordinator()
    val faqFormInputHolder = FaqForm.bindFromRequest()

    if (faqFormInputHolder.hasErrors) {
      Future.successful(Ok(Json.toJson(Error(faqFormInputHolder.toString))))
    } else {
      for {
        user <- loggedInUser
        if user.admin.getOrElse(false)
      } yield {
        val faqData = faqFormInputHolder.get
        val faq = fromFaqData(faqData)

        faqCoordinator.insert(faq)
        Ok(Json.toJson(faq))
      }
    }.recoverWith{
      case ex: NotLoggedInException => Future.successful(Ok(Json.toJson(Error("You are not logged in"))))
    }
  }

  def faqUpdateForUser(faqStringId: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val faqCoordinator = new FaqCoordinator()
    val faqFormInputHolder = FaqForm.bindFromRequest()

    loggedInUser.map{ user =>
      user.admin match {
        case Some(x) if x => // if admin
          val faqData = faqFormInputHolder.get // if there are errors it will throw an expection
          val faq = fromFaqData (faqData)
          if (faqData.faqId.isEmpty) {
            Ok (Json.toJson (Error ("should have a faqId") ) )
          } else {
            val errors = faqCoordinator.update (fromFaqData (faqData) )
              // TODO: add error processing if failed.
            Ok (Json.toJson (faq) )
          }
        case _ =>
          Ok(Json.toJson(Error(faqFormInputHolder.toString)))
      }
    }.recover{
      case ex: NotLoggedInException =>
        Ok(Json.toJson(Error("You are not logged in")))
      case ex: NoSuchElementException if faqFormInputHolder.hasErrors =>
        Ok(Json.toJson(Error(faqFormInputHolder.toString)))
    }
  }

  def productDeleteForUser(faqStringId: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()
    val faqCoordinator = new FaqCoordinator()
    val faqId = UUID.fromString(faqStringId)

    for {
      user <- loggedInUser
      if user.admin.getOrElse(false)
      productShow <- faqCoordinator.delete(faqId)
    } yield {
      Ok(Json.toJson(Error.success))
    }
  }

  def add() = Action.async { request =>
    Future.successful(NotImplemented)
  }

  case class FaqData(faqId:Option[String], orderingIndex: String, title:String, richText: String,
                     author: Option[String],
                     //tags: Option[List[String]],
                     vote: Option[String],
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
      vote = optionInt(faq.vote),
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
      vote = optionInt(faqFormData.vote),
      lastUpdate = Some(DateTime.now()),
      showUntil = optionDateTime(faqFormData.showUntil),
      showFrom = optionDateTime(faqFormData.showFrom))
  }
  def newFaqData(faq: FaqData): Faq = {
    new Faq(
      faqId = optionId(faq.faqId),
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
      "orderingIndex" -> text,
      "title" -> text,
      "richText" -> text,
      "author" -> optional(text),
//      "tags" -> optional(List(text)),
      "vote" -> optional(text),
      "create" -> optional(text),
      "lastUpdate" -> optional(text),
      "showUntil" -> optional(text),
      "showFrom" -> optional(text)
    )(FaqData.apply)(FaqData.unapply)
  )

}
