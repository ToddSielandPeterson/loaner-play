package com.cognitivecreations.dao.mongo.dao.mongomodel

import java.util.UUID

import org.joda.money.Money
import org.joda.time.DateTime

/**
 * Created by Todd Sieland-Peteson on 1/15/15.
 */

case class ShoppingCartMongo(userId:UUID,
                             rentalFrom: DateTime, rentalTo: DateTime,
                             products: List[(String, String)], // list of ids of products
                             subtotal: Money,
                             tax:Money,
                             handling: Money,
                             total: Money
 )

object ShoppingCartMongo{
  implicit lazy val bsonHandler_ShoppingCartMongo = {
    import com.cognitivecreations.helpers.BSONHandlers._
    import com.cognitivecreations.helpers.BSONHelpers._
    reactivemongo.bson.Macros.handler[ShoppingCartMongo]
  }
}