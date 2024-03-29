package controllers

import java.util.UUID

import com.cognitivecreations.dao.mongo.coordinator.{CategoryFlat, CategoryCoordinator, UserCoordinator, ProductCoordinator}
import com.cognitivecreations.modelconverters.CategoryConverter
import com.cognitivecreations.utils.SessionUtils
import controllers.widgets.{Banner, Footer}
import models.{User, Error, UserSession, Product}
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


object UserProductController extends Controller with LoggedInController  {
  import models.Product._

  def productListForUser() = Action.async{ implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val productCoordinator = new ProductCoordinator()

    for {
      user <- loggedInUser
      productList <- productCoordinator.findByOwner(user)
    } yield
      Ok(Json.toJson(productList))
  }

  def productForUser(productStringId: String) = Action.async{ implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val productCoordinator = new ProductCoordinator()

    val productId = UUID.fromString(productStringId)
    for {
      user <- loggedInUser
      product <- productCoordinator.findByPrimaryAndUser(productId, Some(user))
    } yield
      Ok(Json.toJson(product.get))
  }

  def productAddForUser() = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val productCoordinator = new ProductCoordinator()
    val productFormInputHolder = productAddForm.bindFromRequest()

    try {
      if (productFormInputHolder.hasErrors) {
        Future.successful(Ok(Json.toJson(Error(productFormInputHolder.toString))))
      } else {
        for {
          user <- loggedInUser
        } yield {
          val productIn = productFormInputHolder.get
          val product = new Product(userId = user.userId,
            text = productIn.text.get,
            name = productIn.name,
            secondLine = productIn.secondLine,
            categoryId = Some(UUID.fromString(productIn.categoryId)),
            productType = productIn.productType
          )
          productCoordinator.insert(product)
          Ok(Json.toJson(product))
        }
      }
    } catch {
      case ex: NotLoggedInException => Future.successful(Ok(Json.toJson(Error("You are not logged in"))))
    }
  }

  def productUpdateForUser(productStringId: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val productCoordinator = new ProductCoordinator()
    val productFormInputHolder = productForm.bindFromRequest()

    try {
      if (productFormInputHolder.hasErrors) {
        Future.successful(Ok(Json.toJson(Error(productFormInputHolder.toString))))
      } else { // valid
        for {
          user <- loggedInUser
        } yield {
          val productIn = productFormInputHolder.get
          if (productIn.productId.isEmpty) {
            Ok(Json.toJson(Error("should have a productId")))
          } else {
            val product = fromProductData(productIn, user.userId.get)
            val errors = productCoordinator.update(product)
            // TODO: add error processing if failed.
            Ok(Json.toJson(product))
          }
        }
      }
    } catch {
      case ex: NotLoggedInException => Future.successful(Ok(Json.toJson(Error("You are not logged in"))))
    }
  }

  def productDeleteForUser(productStringId: String) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")

    val sessionInfo = SessionUtils(request).fetchFutureSessionInfo()
    val productCoordinator = new ProductCoordinator()
    val productId = UUID.fromString(productStringId)

    for {
      session <- sessionInfo
      productShow <- productCoordinator.delete(productId, session.user)
    } yield {
      Ok(Json.toJson(Error.success))
    }
  }

  /* ******************* */
  /* standard form calls */
  /* ******************* */
  def productList(session: UserSession) = Action.async { implicit request =>
    implicit val simpleDbLookups: ExecutionContext = Akka.system.dispatchers.lookup("contexts.concurrent-lookups")
    val productCoordinator = new ProductCoordinator()

    if (session.user.isDefined) {
      for {
        products <- productCoordinator.findByOwner(session.user.get)
      } yield
        Ok(views.html.user.user_product_list(session))
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
          productCoordinator.findByPrimary(id)
        } else
          Future.successful(Some(Product.newEmptyProduct()))
      } else {
        productCoordinator.findByPrimary(id)
      }

    for {
      product <- futureOptProduct
    } yield {
      Ok(views.html.user.edit_product_body(product.get, session, None))
    }
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
        Ok(views.html.user.user_product_template(headerBody, productBody, footerBody, session))
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
      Ok(views.html.user.user_product_template(headerBody, productEditBody, footerBody, session))
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
        Ok(views.html.user.user_product_template(headerBody, productEditBody, footerBody, session))
      } else {
        Ok(views.html.user.user_product_template(headerBody, productEditBody, footerBody, session))
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
      Ok(views.html.user.user_product_template(headerBody, productShowBody, footerBody, session))
    }
  }

  def add() = Action.async { request =>
    Future.successful(NotImplemented)
  }

  case class ProductData(
    productId: Option[String] = None,
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

  def fromProductData(productIn: UserProductController.ProductData, userId: UUID): Product = {
    new Product(productId = Some(UUID.fromString(productIn.productId.get)),
      userId = Some(userId),
      name = productIn.name,
      secondLine = productIn.secondLine,
      categoryId = Some(UUID.fromString(productIn.categoryId)), // link to unique category id
      productType = None,
      addedDateTime = Some(new DateTime()),
      lastUpdate = Some(new DateTime()),
      pictures = List(),
      thumbnails = List(),
      text = productIn.text.getOrElse(""))
  }

  case class ProductAddData(
                          name: String,
                          secondLine: Option[String] = None,
                          categoryId: String, // link to unique category id
                          productType: Option[String] = None,
                          text: Option[String] = None)

  val productAddForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "secondLine" -> optional(text),
      "categoryId" -> nonEmptyText,
      "productType" -> optional(text),
      "text" -> optional(text)
    )(ProductAddData.apply)(ProductAddData.unapply)
  )

}
