package com.cognitivecreations.modelconvertors

import com.cognitivecreations.dao.mongo.dao.mongomodel.{AddressMongo, ProductMongo}
import models.{Address, Product}
import reactivemongo.bson.BSONObjectID

import scala.Product
import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Todd Sieland-Peteson on 1/11/15.
 */
object AddressConverter {
  def asFutureProductDao(userInF: Future[AddressMongo])(implicit executionContext: ExecutionContext): Future[Address] =
    for (u <- userInF) yield asAddress(u)
  def asFutureProductDaoMongo(userInF: Future[Address])(implicit executionContext: ExecutionContext): Future[AddressMongo] =
    for (u <- userInF) yield asAddressMongo(u)

  def asFutureOptionProductDao(userInF: Future[Option[AddressMongo]])(implicit executionContext: ExecutionContext): Future[Option[Address]] =
    for (u <- userInF) yield if (u.isDefined) Some(asAddress(u.get)) else None
  def asFutureOptionProductDaoMongo(userInF: Future[Option[Address]])(implicit executionContext: ExecutionContext): Future[Option[AddressMongo]] =
    for (u <- userInF) yield if (u.isDefined) Some(asAddressMongo(u.get)) else None

  def asAddress(address: AddressMongo): Address = {
    Address(addressLine1 = address.addressLine1,
      addressLine2 = address.addressLine2,
      city = address.city,
      state = address.state,
      zip = address.zip,
      country = address.country)
  }

  def asAddressMongo(address: Address): AddressMongo = {
    AddressMongo(addressLine1 = address.addressLine1,
      addressLine2 = address.addressLine2,
      city = address.city,
      state = address.state,
      zip = address.zip,
      country = address.country)
  }

}
