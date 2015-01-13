package com.cognitivecreations.dao.mongo


import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.bson._
import reactivemongo.core.commands._
import reactivemongo.api.collections.GenericQueryBuilder
import MongoDao._
import play.api.libs.iteratee.{Enumerator, Iteratee}
import scala.concurrent.duration.Duration
import reactivemongo.api._
import reactivemongo.core.commands.GetLastError
import reactivemongo.bson.BSONInteger
import reactivemongo.api.collections.default.BSONCollection

trait BaseMongoDao[T] extends MongoDao[T] with MongoCollectionCommands {
  implicit val executionContext: ExecutionContext
  val ID: String = "_id"
  lazy val mongoDBUtils: MongoDBUtils = MongoDBUtils
}

trait MongoDao[T] extends Logging with MongoLogging {
  type BSONDocumentHandlerType = BSONDocumentReader[T] with BSONDocumentWriter[T] with BSONHandler[BSONDocument, T]

  implicit val bsonHandler: BSONDocumentHandlerType

  val ID: String
  val collection: BSONCollection

  def readTimeout = Duration(5000, "milliseconds")
  def writeTimeout = Duration(5000, "milliseconds")

  def findById(id: String)(implicit ec: ExecutionContext) : Future[Option[T]] = {
    collection.find(BSONDocument("_id" -> BSONObjectID(id))).domainHeadOption[T]
  }

  def insert(document: T)(implicit ec: ExecutionContext): Future[LastError] = {
    insert(document, GetLastError())
  }

  def insert(document: T, writeConcern: GetLastError)(implicit ec: ExecutionContext): Future[LastError] = {
    implicit val writer = bsonHandler.asInstanceOf[BSONDocumentWriter[T]] // to me this is not necessary, yet the scala compiler insists on it
    println(collection.db.name)
    collection.insert(document, writeConcern)
  }

  def bulkInsert(documents: Seq[T], writeConcern: GetLastError = GetLastError())(implicit ec: ExecutionContext): Future[Int] = {
    implicit val writer = bsonHandler.asInstanceOf[BSONDocumentWriter[T]]
    val bsonDocuments = documents.map(writer.write)
    collection.bulkInsert(Enumerator(bsonDocuments:_*))
  }

  def save(doc: T, writeConcern: GetLastError = GetLastError())(implicit ec: ExecutionContext): Future[(BSONObjectID, LastError)] = {
    val bson = bsonHandler.write(doc)
    val (newID, futureLastError) = bson.get(ID).map { id =>
      val lastError = collection.update(BSONDocument(ID -> id), bson, writeConcern = writeConcern, upsert = true)
      (id.asInstanceOf[BSONObjectID], lastError)

    } getOrElse {
      val id = BSONObjectID.generate
      val lastError = collection.insert(bson.add(ID -> id), writeConcern)
      (id, lastError)
    }

    futureLastError.map { lastError =>
      (newID, lastError)
    }
  }

  def update[S, U](selector: S,update: U, writeConcern: GetLastError = GetLastError(), upsert: Boolean = false, multi: Boolean = false)
                  (implicit selectorWriter: BSONDocumentWriter[S], updateWriter: BSONDocumentWriter[U], ec: ExecutionContext): Future[LastError] = {
    collection.update(selector, update, writeConcern, upsert, multi)
  }

  def count(query: BSONDocument)(implicit ec: ExecutionContext): Future[Int] = collection.db.command(Count(collection.name, Option(query)))

  def countAll(implicit ec: ExecutionContext): Future[Int] = collection.db.command(Count(collection.name, None))

  def find(query: Option[BSONDocument], projection: Option[BSONDocument])(implicit ec: ExecutionContext): Future[List[T]] = {
    collection.find(query.getOrElse(BSONDocument()), projection.getOrElse(BSONDocument())).cursor[T].collect[List]()
  }

  def findWithHint(query: BSONDocument, hint: BSONDocument, projection: Option[BSONDocument] = None)(implicit ec: ExecutionContext): Future[List[T]] = {
    collection.find(query, projection.getOrElse(BSONDocument())).hint(hint).cursor[T].collect[List]()
  }

  def find(query: BSONDocument, projection: BSONDocument)(implicit ec: ExecutionContext): Future[List[T]] = {
    find(Some(query), Some(projection))
  }

  def find(query: BSONDocument)(implicit ec: ExecutionContext): Future[List[T]] = {
    find(Some(query), None)
  }

  def updateQuery(old: T, curr: T): BSONDocument = {
    val oldBson = bsonHandler.write(old)
    val currBson = bsonHandler.write(curr)
    BsonMongoOps.mongo.updateQuery(oldBson, currBson)
  }

  def enumerate(query: BSONDocument)(implicit ec: ExecutionContext): Enumerator[T] = {
    collection.find(query).cursor[T].enumerate()
  }

  def enumerate(implicit ec: ExecutionContext): Enumerator[T] = {
    collection.find(BSONDocument()).cursor[T].enumerate()
  }

  def find(query: BSONDocument, fields: String*)(implicit ec: ExecutionContext): Future[List[BSONDocument]] = {
    collection.find(query, BSONDocument(fields.toList.map(_ -> BSONInteger(1)))).cursor[BSONDocument].collect[List]()
  }

  def findSubVal[P,BV <: BSONValue](
                                     query: BSONDocument,
                                     projection: BSONDocument,
                                     elementName: String
                                     )(implicit
                                       reader:BSONReader[BV, P],
                                       mongoController: ExecutionContext,
                                       bvManifest:Manifest[BV]
                                     ) : Cursor[P] = {

    val subDocReader = new BSONDocumentReader[P] {
      def read(bson: BSONDocument): P = {
        val bv = bson.get(elementName).get
        if(bvManifest.runtimeClass.isInstance(bv)) {
          reader.read(bv.asInstanceOf[BV])
        } else {
          throw new RuntimeException(s"Can't convert $bv to expected type $bvManifest!")
        }
      }
    }

    collection.find(query, projection).cursor[P](subDocReader, mongoController)
  }

  def findSubDoc[P](
                     query: BSONDocument,
                     projection: BSONDocument,
                     elementName: String
                     )(implicit
                       reader:BSONDocumentReader[P],
                       mongoController: ExecutionContext
                     ) : Cursor[P] = {

    val subDocReader = new BSONDocumentReader[P] {
      def read(bson: BSONDocument): P = {
        reader.read(bson.get(elementName).get.asInstanceOf[BSONDocument])
      }
    }

    collection.find(query, projection).cursor[P](subDocReader, mongoController)
  }

  def findAll(implicit ec: ExecutionContext) = find(BSONDocument())

  def findOne(query: BSONDocument)(implicit ec: ExecutionContext): Future[Option[T]] = {
    collection.find(query).one[T]
  }

  def foreach(query: BSONDocument)(processor: T => Unit)(implicit ec: ExecutionContext): Future[Unit]  = {
    collection.find(query).cursor[T].enumerate().run(Iteratee.foreach[T](processor))
  }

  def fold[A](query: BSONDocument, state: A)(processor: (A, T) => A)(implicit ec: ExecutionContext): Future[A] = {
    collection.find(query).cursor[T].enumerate().run(Iteratee.fold(state)(processor))
  }

//  class Async(implicit asyncBuffersConfig: AsyncBuffersConfig) {
//    def progress(task: String)(implicit progressConfig: ProgressConfig) = {
//      new Async()(
//        asyncBuffersConfig.bindProgressConfig(progressConfig.optTask(Some(task)))
//      )
//    }
//
//    def findAll: AsyncBuffers[T] = find[T](BSONDocument(), BSONDocument())
//
//    def find(query: BSONDocument) : AsyncBuffers[T] = find[T](query, BSONDocument())
//
//    def find[P:BSONDocumentReader](query: BSONDocument, projection: BSONDocument) : AsyncBuffers[P] = {
//      import asyncBuffersConfig.executionContext
//
//      def fetchCount() : Future[Int] = collection.db.command(new Count(collection.name, Some(query), Some(projection)))
//
//      AsyncBuffers(fetchCount,{ (skip,batchSize) =>
//        collection.find(query, projection).options(QueryOpts(skip)).cursor[P].collect[Vector](batchSize)
//      })
//    }
//
//    def findSubDoc[P:BSONDocumentReader](query: BSONDocument, projection: BSONDocument, subdocument: String) : AsyncBuffers[P] = {
//      import asyncBuffersConfig.executionContext
//
//      val subDocReader = new BSONDocumentReader[P] {
//        def read(bson: BSONDocument): P = {
//          implicitly[BSONDocumentReader[P]].read(bson.get(subdocument).get.asInstanceOf[BSONDocument])
//        }
//      }
//
//      def fetchCount() : Future[Int] = collection.db.command(new Count(collection.name, Some(query), Some(projection)))
//
//      AsyncBuffers(fetchCount,{ (skip,batchSize) =>
//        collection.find(query, projection).options(QueryOpts(skip)).cursor[P](subDocReader, executionContext).collect[Vector](batchSize)
//      })
//    }
//  }
//
//  def async(implicit asyncBuffersConfig: AsyncBuffersConfig) = new Async

  def backup(i: Int = 1)(implicit ec: ExecutionContext) : Future[String] = {

    val backupCollection = collection.db.collection[BSONCollection](collection.name + i)
    for {
      recordCount <- collection.db.command(Count(backupCollection.name, None))
      collectionName <- {
        if(recordCount == 0) {
          val e = collection.find(BSONDocument(), BSONDocument()).cursor[T].enumerate()
          for {
            _ <- backupCollection.bulkInsert(e)
          } yield backupCollection.name
        } else {
          // Recurse until we find a new backup collection name that isn't in use
          backup(i + 1)
        }
      }
    } yield collectionName
  }
}

object MongoDao {
  implicit class RichCursor(qb: GenericQueryBuilder[BSONDocument,BSONDocumentReader,BSONDocumentWriter]  ) {
    def domainHeadOption[T](implicit executionContext: ExecutionContext, handler: BSONHandler[BSONDocument,T]): Future[Option[T]] = {
      qb.cursor.headOption.map(_.map{ doc =>
        handler.read(doc)
      })
    }
  }
}

trait NaturalKeyDao[T] { self: MongoDao[T] =>

  def  naturalKeySelector(domain: T) : BSONDocument

  def findByNaturalKey(domain: T)(implicit ec: ExecutionContext) : Future[Option[T]] = {
    collection.find(naturalKeySelector(domain)).domainHeadOption[T]
  }

  def upsertByNaturalKey(domain: T)(implicit ec: ExecutionContext) : Future[LastError] = {
    debug(s"Updating Domain ${domain.getClass.getSimpleName} using selector -> ${BSONDocument.pretty(naturalKeySelector(domain))}")
    update(naturalKeySelector(domain), domain, GetLastError(), upsert = true, multi = false)
  }

  def removeByNaturalKey(domain:T)(implicit ec: ExecutionContext):Future[LastError]={
    debug(s"Deleting record ${domain.getClass.getSimpleName} with key ->${BSONDocument.pretty(naturalKeySelector(domain))}")
    collection.remove(naturalKeySelector(domain), GetLastError(), firstMatchOnly = false)
  }

  def insertByNaturalKey(domain: T)(implicit ec: ExecutionContext):Future[LastError]={
    debug(s"Insert if not exists: record ${domain.getClass.getSimpleName} with key ->${BSONDocument.pretty(naturalKeySelector(domain))}")
    update(naturalKeySelector(domain), BSONDocument("$setOnInsert" -> domain), GetLastError(), upsert = true, multi = false)
  }

  def mergeByNaturalKey(domain: T)(implicit ec: ExecutionContext):Future[LastError]={
    debug(s"Merge domain with optional props: record ${domain.getClass.getSimpleName} with key ->${BSONDocument.pretty(naturalKeySelector(domain))}")
    update(naturalKeySelector(domain), BSONDocument("$set" -> domain), GetLastError(), upsert = true, multi = false)
  }
}