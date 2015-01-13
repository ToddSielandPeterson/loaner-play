package com.cognitivecreations.dao.mongo

import reactivemongo.bson.{BSONDocument, BSONString, BSONUndefined, BSONValue}

object BsonMongoOps {

  implicit class RichBSONValue(bson: BSONValue) {
    def \(key: String): BSONValue = bson match {
      case d: BSONDocument =>
        d.get(key) match {
          case Some(v) => v
          case None => BSONUndefined
        }
      case _ => BSONUndefined
    }

    def as[T] = bson.asInstanceOf[T]
  }

  implicit class RichBSONDocument(d: BSONDocument) {
    def value: Map[String, BSONValue] = d.elements.toMap

    def keys: Set[String] = value.keySet
  }

  def mongoPath(p: Seq[String]): String = p.mkString(".")

  def getValue(in: BSONValue, dotPath: String): BSONValue = {
    getValue(in, dotPath.split("\\.").toVector)
  }

  def getValue(in: BSONValue, parents: Seq[String]): BSONValue = {
    parents.foldLeft(in) {
      case (node, key) =>
        val child = node \ key
        if (child.isInstanceOf[reactivemongo.bson.BSONUndefined.type]) {
          BSONUndefined
        } else {
          child
        }
    }
  }

  def deletedKeys(old: BSONValue, curr: BSONValue): Vector[String] = {
    deletedKeys(old, curr, Vector.empty, Vector.empty)
  }

  private def deletedKeys(
                           old: BSONValue,
                           curr: BSONValue,
                           parents: Vector[String],
                           acc: Vector[String]
                           ): Vector[String] = {
    (old, curr) match {
      case (old: BSONDocument, curr: BSONDocument) =>
        old.elements.toVector.map {
          case (key, oldSubDoc: BSONDocument) =>
            deletedKeys(oldSubDoc, curr \ key, parents :+ key, acc)

          case (key, oldSubDoc) =>
            if ((curr \ key).isInstanceOf[BSONUndefined.type]) {
              Vector(mongoPath(parents :+ key))
            } else {
              Vector.empty
            }
        }.flatten.toVector

      case _ =>
        Vector.empty
    }
  }

  def addedFields(old: BSONValue, curr: BSONValue): Vector[(String, BSONValue)] = {
    addedFields(old, curr, Vector.empty, Vector.empty)
  }

  private def addedFields(
                           old: BSONValue,
                           curr: BSONValue,
                           parents: Vector[String],
                           acc: Vector[(String, BSONValue)]
                           ): Vector[(String, BSONValue)] = {
    curr match {
      case curr: BSONDocument =>
        curr.elements.toVector.map {
          case (key, currSubDoc: BSONDocument) if (old \ key).isInstanceOf[BSONDocument] =>
            addedFields(old \ key, currSubDoc, parents :+ key, acc)

          case (key, currSubDoc) =>
            if (currSubDoc != (old \ key)) {
              Vector(mongoPath(parents :+ key) -> currSubDoc)
            } else {
              Vector.empty
            }
        }.flatten.toVector

      case _ =>
        Vector.empty
    }
  }

  object mongo {

    val set = "$set"
    val unset = "$unset"

    def updateQuery(old: BSONValue, curr: BSONValue): BSONDocument = {
      val d = deletedKeys(old, curr)
      val added = addedFields(old, curr)
      createModQuery(d, added)
    }

    def patch(in: BSONDocument, mod: BSONDocument): BSONDocument = {
      val toDelete = (mod \ unset).as[Vector[String]]

      val inAfterDelete = in.value -- toDelete

      val toAdd = (mod \ set).as[BSONDocument].value
      BSONDocument.apply((inAfterDelete ++ toAdd).toSeq)
    }

    /**
     * Cancel fields and keep new
     * @param oldMod
     * @param currMod
     * @return
     */
    def merge(oldMod: BSONValue, currMod: BSONValue): BSONDocument = {
      // TODO add input sanity
      val currDeleted = (currMod \ unset).as[BSONDocument].keys
      val currAdded = (currMod \ set).as[BSONDocument].keys
      val currAll = currDeleted ++ currAdded
      assert(currDeleted.intersect(currAdded).isEmpty, "no common keys in one modification")

      val oldDeletedAfterCurr = (oldMod \ unset).as[BSONDocument].keys diff currAll
      val oldAddedAfterCurr = (oldMod \ set).as[BSONDocument].keys diff currAll
      assert(oldDeletedAfterCurr.intersect(oldAddedAfterCurr).isEmpty, "no common keys in one modification")

      assert(currDeleted.intersect(oldDeletedAfterCurr).isEmpty, "Key cannot be in both deleted")
      assert(currAdded.intersect(oldAddedAfterCurr).isEmpty, "Key cannot be in both added")

      val finalDelete = (currDeleted ++ oldDeletedAfterCurr).toSeq
      val finalAdd = currAdded.toSeq.map {
        key =>
          key ->(currMod \ set \ key)
      } ++ oldAddedAfterCurr.toSeq.map {
        key =>
          key -> (oldMod \ set \ key)
      }
      createModQuery(finalDelete, finalAdd)
    }

    def mergeOldToNew(oldToNew: Seq[BSONDocument]) = {
      oldToNew.reduce(merge)
    }

    def createModQuery(toDelete: Seq[String], toAdd: Seq[(String, BSONValue)]): BSONDocument = {
      BSONDocument(
        unset -> BSONDocument(
          toDelete.map(key => key -> BSONString(""))
        ),
        set -> BSONDocument(
          toAdd
        )
      )
    }

    def getDelete(mod: BSONValue): Vector[String] = {
      mod match {
        case mod: BSONDocument =>
          (mod \ unset).as[BSONDocument].keys.toVector
        case _ =>
          throw new Exception("Invalid mod input")
      }
    }

    def getAdded(mod: BSONValue): Vector[(String, BSONValue)] = {
      mod match {
        case mod: BSONDocument =>
          (mod \ unset).as[BSONDocument].value.toVector
        case _ =>
          throw new Exception("Invalid mod input")
      }
    }



  }

}
