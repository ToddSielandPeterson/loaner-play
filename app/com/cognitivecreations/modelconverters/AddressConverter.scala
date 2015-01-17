package com.cognitivecreations.modelconverters

import java.util.UUID

import com.cognitivecreations.dao.mongo.dao.mongomodel.{AddressMongo, ProductMongo}
import models.{Product, Address}
import reactivemongo.bson.BSONObjectID

import scala.Product
import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Todd Sieland-Peteson on 1/11/15.
 */

trait AddressConverter {
  def fromMongo(address: AddressMongo): Address = {
    Address(addressLine1 = address.addressLine1,
      addressLine2 = address.addressLine2,
      city = address.city,
      state = address.state,
      zip = address.zip,
      country = address.country)
  }

  def toMongo(address: Address): AddressMongo = {
    AddressMongo(addressLine1 = address.addressLine1,
      addressLine2 = address.addressLine2,
      city = address.city,
      state = address.state,
      zip = address.zip,
      country = address.country)
  }

}
