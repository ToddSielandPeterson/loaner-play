package com.cognitivecreations.dao.mongo

import scala.concurrent.ExecutionContext

/**
 * Passes the context class-loader from the originating thread to the next thread in the executor context.
 *
 * NOTE: I'm not sure if this is even a good idea... I just wanted to get this logic out of the MDCExecutionContext
 *       so that I'm not forced to use it in situations where it is not required.
 */
class ContextPassingExecutionContext(delegate: ExecutionContext) extends ExecutionContext with ShutdownSupport {
  def execute(runnable: Runnable): Unit =  delegate.execute(new Runnable {
    def run() {
      //      org.slf4j.MDC.get("session_id")
      val originalClassLoader = Thread.currentThread().getContextClassLoader
      try {
        Thread.currentThread().setContextClassLoader(this.getClass.getClassLoader)
        runnable.run()
      } finally {
        Thread.currentThread().setContextClassLoader(originalClassLoader)
      }
    }
  })

  def reportFailure(t: Throwable): Unit = delegate.reportFailure(t)

  def tryShutdown() {
    tryShutdown(delegate)
  }
}

object ContextPassingExecutionContext {
  def apply(delegate: ExecutionContext) = new ContextPassingExecutionContext(delegate)
}
