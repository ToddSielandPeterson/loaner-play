package com.cognitivecreations.dao.mongo

import scala.collection.JavaConversions._

import assets.PropertiesLookup
import com.mongodb._
import com.mongodb.casbah.commons.conversions.scala._
import com.mongodb.casbah.{MongoCollection, MongoDB}

trait MongoDBUtils extends Logging {
  private[this] var mongo: Option[Mongo] = None

  val dbIdList: List[String] = List("admin")

  lazy val serverAddresses: List[ServerAddress] = PropertiesLookup.getMongoDBURLs.split(",").map(url => new ServerAddress(url)).toList

  def getDbNames = mongo.get.getDatabaseNames

  def replicaSetStatus = mongo match {
    case Some(m) => Option(m.getReplicaSetStatus)
    case _ => None
  }

  def dbFor(dbName: String): Option[DB] = mongo.map(_.getDB(dbName))

  def connectToMongo() {
    try {
      // Avoid tight loop when mongodb server is down
      System.setProperty("com.mongodb.updaterIntervalNoMasterMS", "10000")

      // Serialize Joda
      RegisterJodaTimeConversionHelpers()

      val mongoOpts = MongoClientOptions.builder().legacyDefaults().connectionsPerHost(PropertiesLookup.getMongoConnectionsPerHost).build()//new MongoOptions()
      val credentials = List(MongoCredential.createMongoCRCredential(PropertiesLookup.getMongoDBUser, "admin", PropertiesLookup.getMongoDBPassword.toCharArray))

      mongo = Some(new MongoClient(serverAddresses, credentials, mongoOpts))
//      if (PropertiesLookup.isMongoSlaveOK) mongo.foreach(_.setReadPreference(ReadPreference.secondaryPreferred()))

      dbIdList.foreach({ dbId =>
        new MongoDB(mongo.get.getDB(dbId))
        info("Connecting to mongoDB:  " + serverAddresses.mkString(", ") + "/" + dbId + " with user " + PropertiesLookup.getMongoDBUser)
      })

      ensureIndexes
    } catch {
      case ignore: Throwable => warn("Could not connect to Mongo DB", ignore)
    }
  }

  def disconnectFromMongo() {
    mongo.foreach(_.close())
    mongo = None
  }

  def mongoInstance = mongo

  private val indexes = Seq(
    ("Location Id", "activity", "location", "destinationId"),
    ("Location Parent Id", "activity", "location", "parentId"),
    ("Product Code", "activity", "product", "code"),
    ("Product Status", "activity", "product", "status"),
    ("Product Cache Date", "activity", "product", "cached"),
    ("Product Destination", "activity", "product", "info.basicInfo.destinationId"),
    ("Product Price", "activity", "product", "info.priceInfo.price"),
    ("Product Savings Amount", "activity", "product", "info.priceInfo.savingsAmount"),
    ("Product Discount", "activity", "product", "info.priceInfo.discount")
  )

  private def ensureIndexes {
    info("Possibly building mongo indexes.")
    for(idx <- indexes) {
      info("Ensuring index \"%s\" for %s.%s using \"%s\"".format(idx._1, idx._2, idx._3, idx._4))
      val db = mongo.get.getDB(idx._2)
      val coll = new MongoCollection(db.getCollection(idx._3))
      coll.ensureIndex(idx._4)
    }
  }
}

object MongoDBUtils extends MongoDBUtils

