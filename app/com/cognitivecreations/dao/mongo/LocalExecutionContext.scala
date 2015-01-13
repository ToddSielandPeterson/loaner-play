package com.cognitivecreations.dao.mongo

import scala.concurrent.ExecutionContext
import com.cognitivecreations.dao.mongo.Local._

class LocalExecutionContext(delegate: ExecutionContext, preparedLocalContext: Context) extends ExecutionContext with ShutdownSupport {
  override def prepare(): ExecutionContext = LocalExecutionContext(delegate, Local.save())

  override def execute(runnable: Runnable): Unit = delegate.execute(new LocalRunnable(runnable, preparedLocalContext))

  override def reportFailure(cause: Throwable): Unit = delegate.reportFailure(cause)

  override def tryShutdown(): Unit = tryShutdown(delegate)

  protected class LocalRunnable(runnable: Runnable, preparedLocalContext: Context) extends Runnable {
    def run(): Unit = {
      val prerunLocalContext = Local.save()
      Local.restore(preparedLocalContext)
      try {
        runnable.run()
      } finally {
        Local.restore(prerunLocalContext)
      }
    }
  }
}

object LocalExecutionContext {
  def apply(executionContext: ExecutionContext, preparedLocalContext: Context): LocalExecutionContext = {
    val wrapper = new ExecutionContext {
      override def reportFailure(cause: Throwable): Unit = executionContext.reportFailure(cause)
      override def execute(runnable: Runnable): Unit = executionContext.execute(runnable)
    }
    new LocalExecutionContext(wrapper, preparedLocalContext)
  }

  def apply(executionContext: ExecutionContext): LocalExecutionContext = {
    val localContext = Local.save()
    apply(executionContext, localContext)
  }
}
