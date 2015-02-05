package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.{ProductCoordinator}
import com.cognitivecreations.utils.SessionUtils
import controllers.widgets.{Banner, Footer}
import models.{UserSession, Product}
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}
import ui.Pagelet

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Try

/**
 * Created by tsieland on 1/7/15.
 */
object UserProductController extends Controller {

  def productList(session: UserSession) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val productCoordinator = new ProductCoordinator()

    if (session.user.isDefined) {
      for {
        products <- productCoordinator.findByOwner(session.user.get)
      } yield
        Ok(views.html.user.user_product_list(products, session, None))
    } else
      Future.successful(NotImplemented)
  }

  def productEdit(id: Option[UUID], update: Boolean, session: UserSession) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val productCoordinator = new ProductCoordinator()

    val productFormInputHolder = productForm.bindFromRequest()
    val futureOptProduct: Future[Option[Product]] =
      if (update) {
        if (!productFormInputHolder.hasErrors) {
          val productFormInput = productFormInputHolder.get.productId
          productCoordinator.findByPrimary(UUID.fromString(productFormInput.get))
        } else
          Future.successful(Some(Product.newEmptyProduct()))
      } else {
        productCoordinator.findByPrimary(id)
      }

    for {
      product <- futureOptProduct
    } yield
      Ok(views.html.user.edit_product_body(product, session, None))
  }

  def productShow(id: UUID, session: UserSession) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val productCoordinator = new ProductCoordinator()

    val futureOptProduct: Future[Option[Product]] = productCoordinator.findByPrimary(id)
    for {
      product <- futureOptProduct
    } yield
      Ok(views.html.user.show_product_body(product, session, None))
  }

  def listForUser() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()

    for {
      session <- sessionInfo

      product <- productList(session)(request)
      header  <- Banner.index(embed = true, Some(session))(request)
      footer  <- Footer.index(embed = true, Some(session))(request)

      headerBody  <- Pagelet.readBody(header)
      footerBody  <- Pagelet.readBody(footer)
      productBody <- Pagelet.readBody(product)
    } yield {
      if (session.user.isDefined) // add redirect if user not found
        Ok(views.html.user.user_template(headerBody, productBody, footerBody, session))
      else
        Redirect("/login")
    }
  }

  def edit(id: String) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()
    val productId: Try[UUID] = Try(UUID.fromString(id))

    for {
      session <- sessionInfo
      productEdit <- productEdit(productId.toOption, update = false, session)(request)
      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)

      headerBody <- Pagelet.readBody(header)
      footerBody <- Pagelet.readBody(footer)
      productEditBody <- Pagelet.readBody(productEdit)
    } yield {
      Ok(views.html.user.user_template(headerBody, productEditBody, footerBody, session))
    }
  }

  def editPost(id: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val productFormInputHolder = productForm.bindFromRequest()
    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()
    val productId: Try[UUID] = Try(UUID.fromString(id))

    for {
      session <- sessionInfo

      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)
      productEdit <- productEdit(productId.toOption, update = true, session)(request)

      headerBody <- Pagelet.readBody(header)
      footerBody <- Pagelet.readBody(footer)
      productEditBody <- Pagelet.readBody(productEdit)
    } yield {
      if (!productFormInputHolder.hasErrors) {
        Ok(views.html.user.user_template(headerBody, productEditBody, footerBody, session))
      } else {
        Ok(views.html.user.user_template(headerBody, productEditBody, footerBody, session))
      }
    }
  }

  def delete(id: String) = Action.async { request =>
    Future.successful(NotImplemented)
  }

  def show(id: String) = Action.async { request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()

    for {
      session <- sessionInfo

      header <- Banner.index(embed = true, Some(session))(request)
      footer <- Footer.index(embed = true, Some(session))(request)
      productShow <- productShow(UUID.fromString(id), session)(request)

      headerBody <- Pagelet.readBody(header)
      footerBody <- Pagelet.readBody(footer)
      productShowBody <- Pagelet.readBody(productShow)
    } yield {
      Ok(views.html.user.user_template(headerBody, productShowBody, footerBody, session))
    }
  }

  def add() = Action.async { request =>
    Future.successful(NotImplemented)
  }

  case class ProductData(
    productId: Option[String],
    userId: String,
    name: String,
    secondLine: Option[String] = None,
    categoryId: String, // link to unique category id
    productType: Option[String] = None,
    text: Option[String] = None)

  val productForm = Form(
    mapping(
      "productId" -> optional(text),
      "userId" -> text,
      "name" -> nonEmptyText,
      "secondLine" -> optional(text),
      "categoryId" -> nonEmptyText,
      "productType" -> optional(text),
      "text" -> optional(text)
    )(ProductData.apply)(ProductData.unapply)
  )

}
