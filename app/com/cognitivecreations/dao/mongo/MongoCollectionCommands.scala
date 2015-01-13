package com.cognitivecreations.dao.mongo
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson._
import reactivemongo.core.commands._

trait MongoCollectionCommands extends Logging {

  val collection: BSONCollection

  def distinct[B <: BSONValue, T](key: String, query: Option[BSONDocument])(implicit tReader: BSONReader[B, T]) = {
    val distinctCommand = RawCommand(BSONDocument(
      "distinct" -> collection.name,
      "key"  -> key,
      "query" ->query))
    collection.db.command(distinctCommand).flatMap( doc => {
      CommandError.checkOk(doc, Some("values")) match {
        case Some(error) => Future.failed(error)
        case _ => Future.successful(doc.getAs[Seq[T]]("values").getOrElse(Seq()))
      }
    })
  }

  /**
   * Don't use this unless you have to and delete if Reactive's API supports push with an object.
   * https://groups.google.com/forum/?fromgroups=#!topic/reactivemongo/QI_5SIhbvPU
   */
  def aggregate(pipeline: Seq[BSONDocument], aggregateCollection: BSONCollection = collection) = {
    collection.db.command(Hackgregate(pipeline, aggregateCollection))
  }


  def or(elements: Producer[(String, BSONValue)]*) = {
    BSONDocument("$or" -> elements.foldLeft(BSONArray())((arr, element) => arr ++ BSONDocument(element)))
  }

  def ensureIndex(index: Index): Future[Boolean] = {
    val futureResult = collection.indexesManager.ensure(index)
    futureResult.onComplete {
      case Success(_)  => debug(s"Successfully create a new $index on ${collection.name}")
      case Failure(ex) => warn(s"Failed to create $index for the ${collection.name} collection.", ex)
    }
    futureResult
  }

  def ensureIndexSimple(keysToIndex: Seq[(String, IndexType)]): Future[Boolean] =  {
    val index = Index(key = keysToIndex)
    ensureIndex(index)
  }

  //see aggregate comment
  def multiPush(group: GroupMulti, alias: String, fields: Seq[(String, String)]) = {
    val groupDoc = group.makePipe
    val groupBy = groupDoc.getAs[BSONDocument]("$group").getOrElse(BSONDocument())
    val pushDoc = BSONDocument(alias -> BSONDocument("$push" -> BSONDocument(fields.map(field => field ._1-> BSONString("$" + field._2)))))
    BSONDocument("$group" -> (groupBy ++ pushDoc))
  }

  def multiSet(group: GroupMulti, alias: String, fields: Seq[(String, String)]) = {
    val groupDoc = group.makePipe
    val groupBy = groupDoc.getAs[BSONDocument]("$group").getOrElse(BSONDocument())
    val pushDoc = BSONDocument(alias -> BSONDocument("$addToSet" -> BSONDocument(fields.map(field => field ._1-> BSONString("$" + field._2)))))
    BSONDocument("$group" -> (groupBy ++ pushDoc))
  }

  def multiSet(group: GroupMulti, groupEntries: Seq[(String, Seq[(String, String)])]) = {
    val groupDoc = group.makePipe
    val groupBy = groupDoc.getAs[BSONDocument]("$group").getOrElse(BSONDocument())
    val pushDoc = groupEntries.foldLeft(groupBy) {
      (group, entry) => group ++ BSONDocument(entry._1 -> BSONDocument("$addToSet" -> BSONDocument(entry._2.map(field => field ._1-> BSONString("$" + field._2)))))
    }
    BSONDocument("$group" -> pushDoc)
  }

  private[this] case class Hackgregate(pipeline: Seq[BSONDocument], aggregateCollection: BSONCollection = collection) extends Command[Stream[BSONDocument]] {
    override def makeDocuments =
      BSONDocument(
        "aggregate" -> BSONString(aggregateCollection.name),
        "pipeline" -> BSONArray(
          { for (pipe <- pipeline) yield pipe }.toStream))

    val ResultMaker = Aggregate
  }

  def drop() = collection.drop().fallbackTo(Future(false))

}
