package com.cognitivecreations.helpers

import java.io._
import java.util.UUID
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import com.cognitivecreations.dao.mongo.sugar
import org.joda.time.{DateTime, DateTimeZone}
import reactivemongo.bson._

import scala.xml.{Elem, Source, XML}

object BSONHelpers {
  type BsonDocHandler[A] = BSONDocumentReader[A] with BSONDocumentWriter[A] with BSONHandler[BSONDocument, A]

  //: VariantBSONWriterWrapper[M[A}, BSONArray]
  // Fix holder disgusting reactivemongo bson implicit issue
  implicit def collectionToBsonWriter[A, M[AA] <: Traversable[AA]](implicit aWriter: BSONWriter[A, _ <: BSONValue]) : VariantBSONWriterWrapper[M[A], BSONArray] = new VariantBSONWriterWrapper(implicitly[VariantBSONWriter[M[A], BSONArray]])

//  def getOrDie(message : => String) = self.getOrElse(die(message))

  /** @return a BSON document reader/writer that will fallback on read failure to reading using an older version */
  def mkBsonDocHandler[A](current: BsonDocHandler[A], _zomOldVersion: (BSONDocument => A)*) : BsonDocHandler[A] = new BSONDocumentReader[A] with BSONDocumentWriter[A] with BSONHandler[BSONDocument, A] {
    val zomOldVersion = _zomOldVersion.toList
    def write(a: A): BSONDocument = current.write(a)
    def read(bson: BSONDocument): A = {
      try {
        current.read(bson)
      } catch {
        case ex:Exception =>
          tryOldVersionRead(bson, ex, zomOldVersion)
      }
    }
    def tryOldVersionRead(bson: BSONDocument, original: Exception, zomOldVersion: List[BSONDocument => A]) : A = {
      zomOldVersion match {
        case Nil =>
          throw original
        case head :: tail =>
          try {
            head(bson)
          } catch {
            case ex: Exception =>
              tryOldVersionRead(bson, original, tail)
          }
      }
    }
  }

  implicit lazy val xmlToBsonHandler = new BSONReader[BSONBinary, Elem] with BSONWriter[Elem, BSONBinary] {
    // Note: sax parser isn't thread safe
    val lock = new Object
    val saxParser = XML.parser

    override def read(bson: BSONBinary): Elem = {
      val bytes = bson.value.readArray(bson.value.readable())
      val in = new ByteArrayInputStream(bytes)
      val zipIs = new GZIPInputStream(in)
      val inReader = new InputStreamReader(zipIs, "UTF-8")
      val bufReader = new BufferedReader(inReader)

      lock.synchronized {
        XML.loadXML(Source.fromReader(bufReader), saxParser)
      }
    }

    override def write(t: Elem): BSONBinary = {
      val byteOs = new ByteArrayOutputStream()
      val zipOs = new GZIPOutputStream(byteOs)
      val writer = new OutputStreamWriter(zipOs, "UTF-8")

      XML.write(
        w = writer,
        node = t,
        enc = "UTF-8",
        xmlDecl = true,
        doctype = null
      )

      writer.close()
      BSONBinary(byteOs.toByteArray, Subtype.GenericBinarySubtype)
    }
  }

  implicit val bsonHandler_UUID = new BSONWriter[java.util.UUID,BSONString] with BSONReader[BSONString, java.util.UUID] {
    def write(t: UUID): BSONString = BSONString(t.toString)
    def read(bson: BSONString): UUID = UUID.fromString(bson.toString)
  }

  implicit val bsonHandler_DateTime = new BSONWriter[org.joda.time.DateTime,BSONDateTime] with BSONReader[BSONDateTime, org.joda.time.DateTime] {
    def write(t: DateTime): BSONDateTime = BSONDateTime(t.getMillis)
    def read(bson: BSONDateTime): DateTime = new DateTime(bson.value, DateTimeZone.UTC)
  }

  implicit def bsonHandler_Map[A,B, B_BV <: BSONValue](implicit
                                                       // Note: learned the hard way -- don't even think about using BSONHandler here instead of reader/writer
                                                       aReader: BSONReader[BSONString, A],
                                                       bReader: BSONReader[B_BV, B],
                                                       aWriter: BSONWriter[A, BSONString],
                                                       bWriter: BSONWriter[B, B_BV]
                                                        ) : BSONHandler[BSONDocument, Map[A,B]] = new BSONHandler[BSONDocument, Map[A,B]] {
    def write(m: Map[A,B]): BSONDocument = {
      val tuples = m.toSeq.map { case (a,b) => (aWriter.write(a).value,bWriter.write(b)) }
      BSONDocument(tuples)
    }
    def read(doc: BSONDocument): Map[A,B] = {
      doc.elements
        .map { case (a,b) =>
        (
          aReader.read(BSONString(a)),
          // Note: this is bad juju but I copied it from reactivemongo.bson.DefaultBSONHandlers.BSONArrayCollectionReader
          bReader.read(b.asInstanceOf[B_BV])
          )
      }
        .toMap
    }
  }

  implicit class Collection_PimpMyOption[T](val self: Option[T]) extends AnyVal {
    /** @return the instance if Some otherwise if None, die */
    @inline def getOrDie(message : => String) = self.getOrElse(sugar.die(message))
  }


  implicit def bsonHandler_Tuple2[A,B,A_BV <: BSONValue,B_BV <: BSONValue](implicit
                                                                           // Note: learned the hard way -- don't even think about using BSONHandler here instead of reader/writer
                                                                           aReader: BSONReader[BSONString, A],
                                                                           bReader: BSONReader[B_BV, B],
                                                                           aWriter: BSONWriter[A, BSONString],
                                                                           bWriter: BSONWriter[B, B_BV]
                                                                            ) : BSONHandler[BSONArray, (A,B)] = new BSONHandler[BSONArray, (A,B)] {
    @inline def die(message: String) = {
      error(message)
      throw new RuntimeException(message)
    }

    def write(t: (A,B)): BSONArray = {
      BSONArray(Vector(
        aWriter.write(t._1),
        bWriter.write(t._2)
      ))
    }
    def read(a: BSONArray): (A,B) = {
      (
        a.getAs[A](0).getOrDie(s"Failed to read A from $a!"),
        a.getAs[B](1).getOrDie(s"Failed to read B from $a!")
        )
    }
  }

  /**
   * A type class for converting to and from an optional BSON value. Used to simplify writing BSON serializers.
   * Example: see SimpleHotelDAO
   */
  trait BsonOps[A] {
    type BSONType <: BSONValue
    def toBson(a: A) : Option[BSONValue]
    def fromBson(ov: Option[BSONValue]) : A
  }

  /** @return a standard BsonOps type class instance that utilizes the type's BSONDocumentWriter and BSONDocumentReader */
  def mkBsonOps[A:BSONDocumentWriter:BSONDocumentReader:Manifest] = new BsonOps[A] {
    val className = implicitly[Manifest[A]].runtimeClass.getCanonicalName
    type BSONType = BSONDocument
    def toBson(a: A): Option[BSONValue] = Some(implicitly[BSONDocumentWriter[A]].write(a))
    def fromBson(ov: Option[BSONValue]): A = ov match {
      case Some(bdoc:BSONDocument) => implicitly[BSONDocumentReader[A]].read(bdoc)
      case _ => throw new IllegalArgumentException(s"Failed to parse $className from BSON!")
    }
  }

  class BasicBsonOps[A,B <: BSONValue](implicit writer: BSONWriter[A,B], reader: BSONReader[B,A], manifest: Manifest[B]) extends BsonOps[A] {
    type BSONType = B
    def toBson(a: A) = Some(writer.write(a))

    def fromBson(ov: Option[BSONValue]) = ov match {
      case Some(vs:B) => reader.read(vs)
      case _ => throw new IllegalArgumentException
    }
  }

  def mkBsonEnumOps[A](apply: String => A, unapply: A => Option[String]) = new BSONReader[BSONString, A] with BSONWriter[A,BSONString] with BsonOps[A] {
    type BSONType = BSONString
    def toBson(a: A): Option[BSONValue] = Some(write(a))
    def fromBson(ov: Option[BSONValue]): A = ov match {
      case Some(vs:BSONString) => read(vs)
      case _ => throw new IllegalArgumentException
    }

    def write(a: A): BSONString = BSONString(unapply(a).getOrElse(throw new IllegalArgumentException))
    def read(bson: BSONString): A = apply(bson.value)
  }


  implicit object BsonOpsInt extends BasicBsonOps[Int, BSONInteger]
  implicit object BsonOpsLong extends BasicBsonOps[Long, BSONLong]
  implicit object BsonOpsDouble extends BasicBsonOps[Double, BSONDouble]
  implicit object BsonOpsBoolean extends BasicBsonOps[Boolean, BSONBoolean]
  implicit object BsonOpsString extends BasicBsonOps[String, BSONString]
  // TODO:
  //  implicit object BsonOpsFloat extends BasicBsonOps[Float, BSONDouble]
  //  implicit object BsonOpsByte extends BasicBsonOps[Byte, BSONInteger]
  //  implicit object BsonOpsShort extends BasicBsonOps[Short, BSONInteger]
  //  implicit object BsonOpsJavaSqlDate extends BasicBsonOps[java.sql.Date](java.sql.Date.valueOf)
  //  implicit object BsonOpsJavaUtilDate extends BasicBsonOps[java.util.Date](java.sql.Timestamp.valueOf)
  //  implicit object BsonOpsJavaSqlTimestamp extends BasicBsonOps[java.sql.Timestamp](java.sql.Timestamp.valueOf)
  //  implicit object BsonOpsBigDecimal extends BasicBsonOps[BigDecimal](BigDecimal.apply)
  //  implicit object BsonOpsBigInt extends BasicBsonOps[BigInt](BigInt.apply)


  /**
   * implicit def BsonOps instance for Option[A]
   */
  implicit def optionBsonOps[A](implicit aBsonOps: BsonOps[A]) : BsonOps[Option[A]] = new BsonOps[Option[A]] {
    type BSONType = aBsonOps.BSONType

    def toBson(oa: Option[A]): Option[BSONValue] = oa.map(a => aBsonOps.toBson(a).get)
    def fromBson(ob: Option[BSONValue]): Option[A] = ob.map { b =>
      try {
        aBsonOps.fromBson(ob)
      } catch {
        case t:Throwable =>
          val s =
            b match {
              case bdoc:BSONDocument => BSONDocument.pretty(bdoc)
              case _ => b.toString
            }
          throw new IllegalArgumentException(s"Error while parsing BSON: $s!", t)
      }
    }
  }

  /**
   * Convert anything to an optional BSONValue using the type's implicit BsonOps type class
   */
  implicit class PimpEverything[A](val self: A) extends AnyVal {
    def toBson(implicit ops: BsonOps[A]) : Option[BSONValue] = ops.toBson(self)
  }

  /**
   * Parse a BSONValue using the type's implicit BsonOps type class
   */
  implicit class PimpMyBsonValue(val self: BSONValue) extends AnyVal {
    def parseAs[A](implicit ops: BsonOps[A]) : A = ops.fromBson(Some(self))
  }

  /**
   * Parse a Option[BSONValue] using the type's implicit BsonOps type class
   */
  implicit class PimpMyOptBsonValue(val self: Option[BSONValue]) extends AnyVal {
    def parseAs[A](implicit ops: BsonOps[A]) : A = ops.fromBson(self)
  }

  /**
   * Used to "lift" a Traversable of (String, Option[BSONValue]) pairs to Option[(String,BSONValue)] and then "flatten"
   * by removing empty Option. The output Traversable[(String, BSONValue)] can then be converted to a
   * Map[String,BSONValue] and used to construct a BSONDocument
   * Example: See SimpleHotelDAO
   */
  implicit class PimpMyTraversableBSONValuePairs(val self: Traversable[(String, Option[BSONValue])]) extends AnyVal {
    def liftFlatten : Traversable[(String, BSONValue)] = self.collect { case (key, Some(bsonValue)) =>
      (key, bsonValue)
    }
  }

}
