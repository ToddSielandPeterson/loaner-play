package com.cognitivecreations.helpers

import java.util.UUID

/**
 * Created by Todd Sieland-Peteson on 1/14/15.
 */


object Pimp_UUID {

  implicit def uuidToString(uuid: UUID): String = uuid.toString
  implicit def stringToUUID(uuid: String): UUID = UUID.fromString(uuid)

}
