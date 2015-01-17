package com.cognitivecreations.dao.mongo.dao.mongomodel

import java.util.UUID

import org.joda.time.DateTime

case class UserMongo(lastName: String,
                     firstName: String,
                     email: String,
                     userId: UUID,
                     password: String,
                     website: Option[String] = None,
                     created: Option[DateTime] = None,
                     updated: Option[DateTime] = None,
                     address: Option[AddressMongo] = None)

object UserMongo {
  implicit lazy val bsonHandler_UserMongoProperty = {
    import com.cognitivecreations.helpers.BSONHandlers._
    import com.cognitivecreations.helpers.BSONHelpers._

    reactivemongo.bson.Macros.handler[UserMongo]
  }

  def updateLastName(userMongo: UserMongo, lastName: Option[String]): UserMongo =
    if (lastName.isDefined) userMongo.copy(lastName = lastName.getOrElse("")) else userMongo

  def updateFirstName(userMongo: UserMongo, firstName: Option[String]): UserMongo =
    if (firstName.isDefined) userMongo.copy(firstName = firstName.getOrElse("")) else userMongo

  def updateEmail(userMongo: UserMongo, email: Option[String]): UserMongo =
    if (email.isDefined) userMongo.copy(email = email.getOrElse("")) else userMongo

  def updatePassword(userMongo: UserMongo, password: Option[String]): UserMongo =
    if (password.isDefined) userMongo.copy(lastName = password.getOrElse("")) else userMongo

  def updateWebsite(userMongo: UserMongo, website: Option[String]): UserMongo =
    if (website.isDefined) userMongo.copy(lastName = website.getOrElse("")) else userMongo
}
