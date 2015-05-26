package com.cognitivecreations.dao.mongo.dao.mongomodel

import com.cognitivecreations.dao.mongo.Logging
import reactivemongo.api.collections.default.BSONCollection

import scala.concurrent.ExecutionContext

object DatabaseMongoDataSource extends MongoDataSource {

    val dbName = "toolloaner"

    // Application
    lazy val productsDataSource = db[BSONCollection]("products")
    lazy val userDataSource = db[BSONCollection]("users")
    lazy val categorySource = db[BSONCollection]("category")
    lazy val pagesSource = db[BSONCollection]("pages")
    lazy val sessionSource = db[BSONCollection]("session")
    lazy val categoryTreeSource = db[BSONCollection]("categoryTree")
    lazy val shoppingCartSource = db[BSONCollection]("shoppingCart")
    lazy val userReviewsSource = db[BSONCollection]("userReviews")
    lazy val productReviewsSource = db[BSONCollection]("productReviews")
    lazy val ordersSource = db[BSONCollection]("orders")
    lazy val privateMessageSource = db[BSONCollection]("privateMessages")
    lazy val image = db[BSONCollection]("images")
    lazy val faq = db[BSONCollection]("faq")
}
