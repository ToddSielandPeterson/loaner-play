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
}
