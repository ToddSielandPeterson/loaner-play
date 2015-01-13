package com.cognitivecreations.dao.mongo

import assets.PropertiesLookup
import play.api.libs.json.{Json, Writes}
import reactivemongo.api.{DB, DefaultDB, MongoDriver}
import reactivemongo.core.nodeset.Authenticate

import scala.concurrent.{Future, ExecutionContext}


trait MongoDataSource {
  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
  val dbName: String
  lazy val db = MongoDBManager.dbFactory(dbName)
}

case class MongoDBStatus(name: String, user: String, authenticated: Boolean, error: Option[String] = None)

object MongoDBManager extends Logging {
  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
  implicit val mongoDbStatusWriter: Writes[MongoDBStatus] = Json.writes[MongoDBStatus]

  lazy val names = Seq("admin")
  lazy val nodes = PropertiesLookup.getMongoDBURLs.split(",").toSeq
  lazy val driver = new MongoDriver

  lazy val auth = names.map(name => Authenticate(name, PropertiesLookup.getMongoDBUser, PropertiesLookup.getMongoDBPassword))
  lazy val connection = driver.connection(nodes = nodes, authentications = auth)

  def dbFactory(name: String): DefaultDB = DB(name, connection)

  /**
   * The DB map and statusChecks method exist for the HealthController to allow fetching collection names to test authentication.
   */
  private val dbs: Map[String, DefaultDB] = names.map(name => (name, dbFactory(name))).toMap

  def connect {
    connection
    log.info("Connecting to mongo dbs: " + MongoDBManager.names.mkString(","))
  }

  def statusChecks: Future[List[MongoDBStatus]] = Future.sequence {
    dbs.map { nameDbPair =>
      val (name, db) = nameDbPair
      db.collectionNames.map(_ match {
        case _ => MongoDBStatus(name, PropertiesLookup.getMongoDBUser, true)
      }).recover{ case ex: Throwable => MongoDBStatus(name, PropertiesLookup.getMongoDBUser, false, Option(ex.getMessage)) }
    }.toList
  }
}