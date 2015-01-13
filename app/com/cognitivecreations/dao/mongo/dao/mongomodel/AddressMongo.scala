package com.cognitivecreations.dao.mongo.dao.mongomodel

import com.cognitivecreations.helpers.BSONHelpers

/**
 * Created by tsieland on 1/8/15.
 */
case class AddressMongo(addressLine1: String,
                        addressLine2: Option[String],
                        city: String,
                        state: String,
                        zip: String,
                        country: Option[String])

object AddressMongo {
  implicit lazy val bsonHandler_AddressMongoProperty = {
    import com.cognitivecreations.helpers.BSONHelpers._
    reactivemongo.bson.Macros.handler[AddressMongo]
  }
}
