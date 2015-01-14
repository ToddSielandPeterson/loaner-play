package com.cognitivecreations.modelconverters

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.UserMongo
import models.{Address, User}
import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Todd Sieland-Peteson on 1/11/15.
 */

class UserConverter extends ModelConverterBase[User, UserMongo] with AddressConverter {

  def fromMongo(userIn: UserMongo): User = {
    User(
      firstName = userIn.firstName,
      lastName = userIn.lastName,
      email = userIn.email,
      userId = Some(UUID.fromString(userIn.userId)),
      password = None,
      website = userIn.website,
      address = fromMongo(userIn.address.get))
  }

  def toMongo(userIn: User): UserMongo = {
    UserMongo(firstName = userIn.firstName,
      lastName = userIn.lastName,
      email = userIn.email,
      userId = userIn.userId.getOrElse(UUID.randomUUID()).toString,
      password = userIn.password.getOrElse(""),
      website = userIn.website,
      address = Some(toMongo(userIn.address))
    )
  }


  def asUserWithHidden(userIn: UserMongo): User = {
    User(
      firstName = userIn.firstName,
      lastName = userIn.lastName,
      email = userIn.email,
      userId = Some(UUID.fromString(userIn.userId)),
      password = Some(userIn.password),
      website = userIn.website,
      address = fromMongo(userIn.address.get)
    )
  }

  def asUserMongoWithHidden(userIn: User): UserMongo = {
    UserMongo(firstName = userIn.firstName,
      lastName = userIn.lastName,
      email = userIn.email,
      userId = userIn.userId.getOrElse(UUID.randomUUID()).toString,
      password = userIn.password.getOrElse(""),
      website = userIn.website,
      address = Some(toMongo(userIn.address))
    )
  }

}
