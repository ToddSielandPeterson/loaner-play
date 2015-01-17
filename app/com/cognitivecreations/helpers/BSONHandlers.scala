package com.cognitivecreations.helpers

import java.util.UUID

import org.joda.money._
import org.joda.time._
import reactivemongo.bson._

/**
 * Created by Todd Sieland-Peteson on 1/11/15.
 */
object BSONHandlers extends BSONHandlers

trait BSONHandlers {
  object ObjectIdToStringHandler extends BSONHandler[BSONObjectID, String]{
    def write(t: String): BSONObjectID = BSONObjectID(t)
    def read(bson: BSONObjectID): String = bson.stringify
  }

  implicit object DateHandler extends BSONHandler[BSONDateTime, DateTime]{
    def write(t: DateTime): BSONDateTime = BSONDateTime(t.getMillis)
    def read(bson: BSONDateTime): DateTime = new DateTime(bson.value)
  }

  implicit object LocalDateHandler extends BSONHandler[BSONDateTime, LocalDate] {
    def write(t: LocalDate): BSONDateTime = BSONDateTime(t.toDateTimeAtStartOfDay(DateTimeZone.forID("UTC")).getMillis)
    def read(bson: BSONDateTime): LocalDate = new LocalDate(bson.value, DateTimeZone.forID("UTC"))
  }

  implicit object LocalTimeHandler extends BSONHandler[BSONInteger, LocalTime]{
    def write(t: LocalTime): BSONInteger = BSONInteger(t.getMillisOfDay)
    def read(bson: BSONInteger): LocalTime = LocalTime.fromMillisOfDay(bson.value)
  }

  implicit object LocalDateTimeHandler extends BSONHandler[BSONDateTime, LocalDateTime]{
    def write(t: LocalDateTime): BSONDateTime = BSONDateTime(t.toDateTime(DateTimeZone.forID("UTC")).getMillis)
    def read(bson: BSONDateTime): LocalDateTime = new LocalDateTime(bson.value, DateTimeZone.forID("UTC"))
  }

  implicit object BigDecimalHandler extends BSONHandler[BSONString, BigDecimal]{
    def write(t: BigDecimal): BSONString = BSONString(t.toString)
    def read(bson: BSONString): BigDecimal = BigDecimal.apply(bson.value)
  }

  implicit object RangeHandler extends BSONHandler[BSONDocument, Range]{
    def write(range: Range): BSONDocument = BSONDocument("min" -> range.min, "max" -> range.max)

    def read(bson: BSONDocument): Range =  Range(bson.getAs[Int]("min").get, bson.getAs[Int]("max").get+1)
  }

  def enumHandler[T <: Enumeration](enum: T) = new BSONHandler[BSONString, T#Value] {
    def write(t: T#Value): BSONString = BSONString(t.toString)
    def read(bson: BSONString): T#Value = enum.values.find(_.toString == bson.value).getOrElse(throw new RuntimeException("Invalid enum value "+bson.value))
  }

  def javaEnumHandler[T <: Enum[T]](enumClass: Class[T]) = new BSONHandler[BSONString, T] {
    def write(t: T) = BSONString(t.toString)
    def read(bson: BSONString) = Enum.valueOf(enumClass, bson.value)
  }

  def documentSeqHandler[T](implicit writer: BSONWriter[T, BSONDocument], reader: BSONReader[BSONDocument, T] ) = new BSONHandler[BSONArray, Seq[T]]{
    def write(items: Seq[T]): BSONArray = BSONArray(items.map(writer.write(_)))
    def read(bson: BSONArray): Seq[T] = bson.stream.filter(_.isSuccess).map(bval => bval.get.asInstanceOf[BSONDocument].as[T]).toSeq
  }

  implicit object MoneyChanger extends BSONHandler[BSONDocument,Money]{
    def write(money: Money): BSONDocument = BSONDocument(
      "currency" -> money.getCurrencyUnit.getCurrencyCode,
      "amount" -> money.getAmountMinorInt
    )

    def read(bson: BSONDocument) : Money = {
      val currencyUnit = CurrencyUnit.getInstance(bson.getAs[String]("currency").getOrElse("USD"))
      val rawAmount = bson.getAs[Int]("amount").getOrElse(throw new RuntimeException("could not find amount in money"))
      Money.ofMinor(currencyUnit, rawAmount)
    }
  }

  implicit object BigMoneyHandler extends BSONHandler[BSONDocument, BigMoney]{
    def write(money: BigMoney): BSONDocument = BSONDocument(
      "currency" -> money.getCurrencyUnit.getCurrencyCode,
      "amount" -> money.getAmountMinorLong
    )

    def read(bson: BSONDocument) : BigMoney = {
      val currencyUnit = CurrencyUnit.getInstance(bson.getAs[String]("currency").getOrElse("USD"))
      val rawAmount = bson.getAs[Long]("amount").getOrElse(throw new RuntimeException("could not find amount in money"))
      BigMoney.ofMinor(currencyUnit, rawAmount)
    }
  }

  def eitherHandler[L,R](implicit leftHandler: BSONHandler[BSONValue,L], rightHandler: BSONHandler[BSONValue,R]) = new BSONHandler[BSONDocument,Either[L,R]] {
    def write(t: Either[L, R]): BSONDocument = {
      t match {
        case Left(v)  => BSONDocument("left"  -> leftHandler.write(v))
        case Right(v) => BSONDocument("right" -> rightHandler.write(v))
      }
    }

    def read(bson: BSONDocument): Either[L, R] = {
      bson.get("left").map( b=> Left(leftHandler.read(b))).getOrElse{
        bson.get("right").map(b=> Right(rightHandler.read(b))).get
      }
    }
  }

  implicit object CurrencyUnitHandler extends BSONHandler[BSONDocument,CurrencyUnit]{
    def write(c: CurrencyUnit): BSONDocument = BSONDocument("currency" -> c.getCode)
    def read (bson: BSONDocument) : CurrencyUnit = {
      CurrencyUnit.of(bson.getAs[String]("currency").get)
    }
  }

  implicit object UUIDHandler extends BSONHandler[BSONString,UUID]{
    def write(c: UUID): BSONString = BSONString(c.toString)
    def read (bson: BSONString) : UUID = UUID.fromString(bson.value)
  }

  implicit def implicitMapHandler[B <: BSONValue, T ](implicit handler: BSONHandler[B, T]) : BSONHandler[BSONDocument,Map[String, T]] = new BSONHandler[BSONDocument,Map[String, T]]{
    def write(data: Map[String, T]): BSONDocument =
      BSONDocument(data.map(mapEntry => mapEntry._1 -> handler.write(mapEntry._2)))

    def read(bson: BSONDocument): Map[String, T] =
      Map(bson.elements.map(elementToTuple):_*)

    def elementToTuple(element: BSONElement) =
      element._1 -> element._2.asInstanceOf[B].as[T](handler)
  }

  implicit def stringToSeqMapHandler[B <: BSONDocument, T ](implicit handler: BSONHandler[B, T]) : BSONHandler[BSONDocument,Map[String, Seq[T]]] = new BSONHandler[BSONDocument,Map[String, Seq[T]]] {
    def write(data: Map[String, Seq[T]]): BSONDocument =
      BSONDocument(data.map(mapEntry => mapEntry._1 -> BSONArray(mapEntry._2.map(handler.write(_)))))

    def read(bson: BSONDocument): Map[String, Seq[T]] = {

      Map(bson.elements.map(elementToTuple):_*)
    }

    def elementToTuple(element: BSONElement):(String,Seq[T]) ={

      element._1 -> element._2.asInstanceOf[BSONArray].stream.filter(_.isSuccess).map(bval => bval.get.asInstanceOf[B].as[T](handler)).toSeq
    }
  }


  implicit val StringMapHandler = implicitMapHandler[BSONString, String]
}