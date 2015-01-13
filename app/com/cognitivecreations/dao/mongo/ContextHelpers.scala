package com.cognitivecreations.dao.mongo

import scala.concurrent.ExecutionContext

/**
 * Helper around implicit contexts.
 *
 * @author Pedro De Almeida (almeidap)
 */
trait ContextHelper {

  implicit def ec: ExecutionContext = ExecutionContext.Implicits.global

}