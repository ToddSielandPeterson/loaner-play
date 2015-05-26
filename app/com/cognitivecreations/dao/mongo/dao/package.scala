package com.cognitivecreations.dao.mongo

import java.util.UUID

/**
 * Created by Todd Sieland-Peteson on 5/25/15.
 */
package object dao {

  implicit def convert_UUIDToString(uuid: UUID): String = uuid.toString
  implicit def convert_StringToUUID(uuid: String): String = UUID.fromString(uuid)
}
