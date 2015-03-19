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
      userId = Some(userIn.userId),
      password = None,
      address = fromMongo(userIn.address.get),
      admin = userIn.admin
    )
  }

  def toMongo(userIn: User): UserMongo = {
    UserMongo(firstName = userIn.firstName,
      lastName = userIn.lastName,
      email = userIn.email,
      userId = userIn.userId.getOrElse(UUID.randomUUID()),
      password = userIn.password.getOrElse(""),
      address = Some(toMongo(userIn.address)),
      admin = userIn.admin
    )
  }


  def asUserWithHidden(userIn: UserMongo): User = {
    User(
      firstName = userIn.firstName,
      lastName = userIn.lastName,
      email = userIn.email,
      userId = Some(userIn.userId),
      password = Some(userIn.password),
      address = fromMongo(userIn.address.get),
      admin = userIn.admin
    )
  }

  def asUserMongoWithHidden(userIn: User): UserMongo = {
    UserMongo(firstName = userIn.firstName,
      lastName = userIn.lastName,
      email = userIn.email,
      userId = userIn.userId.getOrElse(UUID.randomUUID()),
      password = userIn.password.getOrElse(""),
      address = Some(toMongo(userIn.address)),
      admin = userIn.admin
    )
  }

}
