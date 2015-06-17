package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.{PrivateMessageCoordinator, FaqCoordinator, UserCoordinator}
import com.cognitivecreations.modelconverters.CategoryConverter
import com.cognitivecreations.utils.SessionUtils
import controllers.widgets.{Banner, Footer}
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


object PrivateMessageController extends Controller with LoggedInController  {
  import models.Faq._

  def privateMessageList() = Action.async{ implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val faqCoordinator = new FaqCoordinator()

    for {
      user <- loggedInUser
      faqList <- faqCoordinator.findAllActive()
    } yield
      Ok(Json.toJson(faqList))
  }

//  def privateMessageList(page:Int, size:Int) = Action.async{ implicit request =>
//    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
//    val privateMessageCoordinator = new PrivateMessageCoordinator()
//
//    for {
//      user <- loggedInUser
//      pmList <- privateMessageCoordinator.find()
//    } yield
//      Ok(Json.toJson(pmList))
//  }

  def privateMessage(id: String) = Action.async{ implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val privateMessageCoordinator = new PrivateMessageCoordinator()

    for {
      user <- loggedInUser
      pmList <- privateMessageCoordinator.findByPrimary(UUID.fromString(id))
    } yield
      Ok(Json.toJson(pmList))
  }

  def addPrivateMessageForUser() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val privateMessageCoordinator = new PrivateMessageCoordinator()
    val privateMessageFormInputHolder = privateMessageForm.bindFromRequest()

    try {
      if (privateMessageFormInputHolder.hasErrors) {
        Future.successful(Ok(Json.toJson(Error(privateMessageFormInputHolder.toString))))
      } else {
        for {
          user <- loggedInUser
        } yield {
          val messageData = privateMessageFormInputHolder.get
          val privateMessage = fromPrivateMessageData(messageData)
          privateMessageCoordinator.insert(privateMessage)
          Ok(Json.toJson(privateMessage))
        }
      }
    } catch {
      case ex: NotLoggedInException => Future.successful(Ok(Json.toJson(Error("You are not logged in"))))
    }
  }

  // fix this
  def savePrivateMessage() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val privateMessageCoordinator = new PrivateMessageCoordinator()
    val privateMessageFormInputHolder = privateMessageForm.bindFromRequest()

    try {
      if (privateMessageFormInputHolder.hasErrors) {
        Future.successful(Ok(Json.toJson(Error(privateMessageFormInputHolder.toString))))
      } else {
        for {
          user <- loggedInUser
        } yield {
          val messageData = privateMessageFormInputHolder.get
          val privateMessage = fromPrivateMessageData(messageData)
          privateMessageCoordinator.insert(privateMessage)
          Ok(Json.toJson(privateMessage))
        }
      }
    } catch {
      case ex: NotLoggedInException => Future.successful(Ok(Json.toJson(Error("You are not logged in"))))
    }
  }

  def replyPrivateMessageForUser(faqStringId: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val privateMessageCoordinator = new PrivateMessageCoordinator()
    val privateMessageFormInputHolder = privateMessageForm.bindFromRequest()

    {
      if (privateMessageFormInputHolder.hasErrors) {
        Future.successful(Ok(Json.toJson(Error(privateMessageFormInputHolder.toString))))
      } else {
        {
          {
            for {
              user <- loggedInUser
            } yield {
              val privateMessageIn = privateMessageFormInputHolder.get
              if (privateMessageIn.id.isEmpty) {
                Future(Ok(Json.toJson(Error("should have a private message id"))))
              } else {
                val privateMessage = fromPrivateMessageData(privateMessageIn)
                privateMessageCoordinator.update(privateMessage).map {
                  case z if z.inError => Ok(Json.toJson(Error("You are not logged in")))
                  case _ => Ok(Json.toJson(privateMessage))
                }
                //            Ok(Json.toJson(Error("should have a private message id")))
              }
            }
          }.flatMap(y1 => y1)
        }
      }.recover {
        case ex: NotLoggedInException => Ok(Json.toJson(Error("You are not logged in")))
      }
    }
  }

  def privateMessageDeleteForUser(faqStringId: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()
    val faqCoordinator = new FaqCoordinator()
    val faqId = UUID.fromString(faqStringId)

    for {
      session <- sessionInfo
      productShow <- faqCoordinator.delete(faqId)
    } yield {
      Ok(Json.toJson(Error.success))
    }
  }

  def add() = Action.async { request =>
    Future.successful(NotImplemented)
  }

  case class PrivateMessageData(id: Option[String] = None,
                                fromUser: Option[String] = None,
                                toUser: Option[String],
                                subject: String,
                                body: String,
                                //attachments: List[String] = List(),
                                newMessage: Option[Boolean],
                                read: Option[Boolean],
                                replyId: Option[String], // uuid of Private Message back
                                created: Option[String],
                                updated: Option[String],
                                opened: Option[String])

  val privateMessageForm = Form(
    mapping(
      "id" -> optional(text),
      "fromUser" -> optional(text),
      "toUser" -> optional(text),
      "subject" -> nonEmptyText,
      "body" -> nonEmptyText,
//      "attachments" -> List(String),
      "newMessage" -> optional(boolean),
      "read" -> optional(boolean),
      "replyId" -> optional(text),
      "created" -> optional(text),
      "updated" -> optional(text),
      "opened" -> optional(text)
    )(PrivateMessageData.apply)(PrivateMessageData.unapply)
  )

  def fromPrivateMessageData(privateMessage: PrivateMessageController.PrivateMessageData): PrivateMessage = {
    new PrivateMessage(
      id = Some(UUID.fromString(privateMessage.id.get)),
      fromUser = Some(UUID.fromString(privateMessage.fromUser.get)),
      toUser = Some(UUID.fromString(privateMessage.toUser.get)),
      subject = privateMessage.subject,
      body = privateMessage.body,
      //attachments = privateMessage.attachments,
      newMessage = privateMessage.newMessage,
      read = Some(privateMessage.read.get),
      replyId = Some(UUID.fromString(privateMessage.replyId.get)),
      created = privateMessage.created match {
        case Some(x) => Some(DateTime.parse(x))
        case None => None
      },
      opened = privateMessage.opened match {
        case Some(x) => Some(DateTime.parse(x))
        case None => None
      },
      updated = Some(DateTime.now())
      )
  }

}
