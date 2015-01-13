package com.cognitivecreations.dao.mongo

import scala.concurrent.{ExecutionContextExecutorService, ExecutionContext}

trait ShutdownSupport extends Logging {

  def tryShutdown()

  protected def tryShutdown(ec: ExecutionContext) {
    if (ec.isInstanceOf[ExecutionContextExecutorService]) {
      ec.asInstanceOf[ExecutionContextExecutorService].shutdown()
    } else if (ec.isInstanceOf[ShutdownSupport]) {
      ec.asInstanceOf[ShutdownSupport].tryShutdown()
    } else {
      warn("Shutdown is not supported")
    }

  }
}
