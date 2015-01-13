package com.cognitivecreations.dao.mongo

import java.util.concurrent._
import java.util.concurrent.atomic.AtomicInteger


import com.cognitivecreations.dao.mongo

import scala.concurrent.{ExecutionContextExecutorService, ExecutionContext}

/**
 * ExecutionContext decorator that automatically propagates the MDC session id when using scala Futures.
 */
class MDCExecutionContext(delegate: ExecutionContext, preparedMDCBag: MDCBag) extends ExecutionContext with ShutdownSupport {
  def execute(runnable: Runnable): Unit = delegate.execute(MDCRunnable(getMDCBag, runnable))

  def reportFailure(t: Throwable): Unit = delegate.reportFailure(t)

  override def prepare(): ExecutionContext = MDCExecutionContext(delegate, MDCBag(Option(mongo.getMDCSessionId), mongo.getMDCProduct, mongo.getMDCGroupId))

  protected def getMDCBag: MDCBag = preparedMDCBag

  protected def withMDC[T](sessionId: String, productOpt: Option[String], groupIdOpt: Option[String])(block: => T): T = {
    mongo.setMDC(sessionId, productOpt, groupIdOpt)
    try {
      block
    } finally {
      mongo.unsetMDC
    }
  }

  protected class MDCRunnable(sessionId: String, productOpt: Option[String], groupIdOpt: Option[String], delegate: Runnable) extends Runnable {
    def run() = withMDC(sessionId, productOpt, groupIdOpt) { delegate.run() }
  }

  protected object MDCRunnable {
    def apply(sessionId: String, productOpt: Option[String], groupIdOpt: Option[String], delegate: Runnable): Runnable = new MDCRunnable(sessionId, productOpt, groupIdOpt, delegate)
    def apply(mdcBag: MDCBag, delegate: Runnable): Runnable = {
      mdcBag.sessionId match {
        case Some(sessionId) => apply(sessionId, mdcBag.productOpt, mdcBag.groupIdOpt, delegate)
        case None => delegate
      }
    }
  }

  def tryShutdown() {
    tryShutdown(delegate)
  }
}

object MDCExecutionContext extends Logging {
  /**
   * Global ExecutionContext decorated with MDC propagation behaviour.
   */
  def global = Implicits.global

  object Implicits {
    /**
     * This is the implicit global ExecutionContext decorated with MDC propagation behaviour,
     * import this when you want to provide the global ExecutionContext implicitly
     */
    implicit lazy val global = MDCExecutionContext(ExecutionContext.global)

    /**
     * This is the implicit global play ExecutionContext decorated with MDC propagation behavior.
     */
    implicit lazy val playGlobal = MDCExecutionContext(play.api.libs.concurrent.Execution.Implicits.defaultContext)
  }

  val uncaughtExceptionReporter = { throwable:Throwable => error("Uncaught exception from Future",throwable) }

  /**
   * Decorate an existing ExecutionContext with MDC propagation behaviour.
   */
  def apply(executionContext: ExecutionContext, preparedMDCBag: MDCBag): MDCExecutionContext = {
    val wrapper = LocalExecutionContext(new ExecutionContext {
      override def reportFailure(t: Throwable): Unit = {
        uncaughtExceptionReporter(t)
        executionContext.reportFailure(t)
      }
      override def execute(runnable: Runnable): Unit = executionContext.execute(runnable)
    })
    new MDCExecutionContext(wrapper, preparedMDCBag)
  }

  /**
   * Decorate an existing ExecutionContext with MDC propagation behaviour.
   */
  def apply(executionContext: ExecutionContext): MDCExecutionContext = apply(executionContext, MDCBag(Option(mongo.getMDCSessionId), mongo.getMDCProduct, mongo.getMDCGroupId))

  /**
   * All of the following apply methods take a poolName argument used to set the thread name prefix for all threads in a pool.
   * Subsequent overloads exist to allow various sources for the underlying pool implementation.
   * Whatever the pool is gets wrapped in an ExecutionContext and decorated with MDC propagation behavior.
   */
  def apply(poolName: String): MDCExecutionContext = apply(
    ContextPassingExecutionContext(
      ExecutionContext.fromExecutorService(
        e = Executors.newCachedThreadPool(new BasicThreadFactory(poolName)),
        reporter = uncaughtExceptionReporter
      )
    )
  )

  def apply(poolName: String, threadPoolExecutor: ThreadPoolExecutor): MDCExecutionContext = {
    threadPoolExecutor.setThreadFactory(new BasicThreadFactory(poolName))
    apply(
      ContextPassingExecutionContext(
        ExecutionContext.fromExecutorService(
          e = threadPoolExecutor,
          reporter = uncaughtExceptionReporter
        )
      )
    )
  }

  def apply(poolName: String, fixedSize: Int): MDCExecutionContext = {
    apply(
      ContextPassingExecutionContext(
        ExecutionContext.fromExecutorService(
          e = Executors.newFixedThreadPool(fixedSize, new BasicThreadFactory(poolName)),
          reporter = uncaughtExceptionReporter
        )
      )
    )
  }
}
//abstract class DualMDCExecutionContext(delegate: ExecutionContext)
//  extends MDCExecutionContext(delegate, MDCBag(Option(util.getMDCSessionId), util.getMDCProduct, util.getMDCGroupId)) with BlockingExecutionContext with ShutdownSupport {
//  override def tryShutdown() = {
//    super.tryShutdown()
//    tryShutdown(blockingExecutionContext)
//  }
//}

//object DualMDCExecutionContext extends Logging {
//  lazy val forTesting = DualMDCExecutionContext("test")
//  val uncaughtExceptionReporter = { throwable:Throwable =>
//    error("Uncaught exception from Future",throwable)
//  }
//
//  def apply(poolNamePrefix: String, coreSize: Int): DualMDCExecutionContext = {
//    val nonBlockingPoolNamePrefix = poolNamePrefix + "-nb"
//    apply(poolNamePrefix, Executors.newFixedThreadPool(coreSize, new BasicThreadFactory(nonBlockingPoolNamePrefix)))
//  }
//
//
//  def apply(poolNamePrefix: String, executor: ExecutorService, blockingExpansionSize: Int = Int.MaxValue): DualMDCExecutionContext = {
//    // if you change these two prefixes, make sure you find and update all assertions that use these
//    val nonBlockingPoolNamePrefix = poolNamePrefix + "-nb"
//    val blockingPoolNamePrefix = poolNamePrefix + "-b"
//    // the blocking execution context is configured as cached thread pool
//    // the attempt to used a thread pool with min size failed.
//    // the below code results in a thread pool that never grows.
//    //    val blockingSubExecutionContext = MDCExecutionContext(
//    //      blockingPoolNamePrefix,
//    //      new ThreadPoolExecutor(coreSize, blockingExpansionSize, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue[Runnable]())
//    //    )
//    val blockingSubExecutionContext = MDCExecutionContext(blockingPoolNamePrefix)
//
//    executor match {
//      case tp: ThreadPoolExecutor => tp.setThreadFactory(new BasicThreadFactory(nonBlockingPoolNamePrefix))
//    }
//    new DualMDCExecutionContext(ContextPassingExecutionContext(
//      ExecutionContext.fromExecutorService(
//        e = executor,
//        reporter = uncaughtExceptionReporter
//      )
//    )) {
//      def blockingExecutionContext = blockingSubExecutionContext
//    }
//  }
//
//  def apply(poolNamePrefix: String): DualMDCExecutionContext =
//    DualMDCExecutionContext(poolNamePrefix, Runtime.getRuntime.availableProcessors * 2)
//}

class MDCExecutionContextExecutorService(delegate: ExecutionContextExecutorService, preparedMDCBag: MDCBag)
  extends MDCExecutionContext(delegate, preparedMDCBag) with ExecutionContextExecutorService {

  import scala.collection.JavaConverters._

  def shutdown() = delegate.shutdown()

  def shutdownNow(): java.util.List[Runnable] = delegate.shutdownNow

  def isShutdown: Boolean = delegate.isShutdown

  def isTerminated: Boolean = delegate.isTerminated

  def awaitTermination(timeout: Long, unit: TimeUnit): Boolean = delegate.awaitTermination(timeout, unit)

  def submit[T](task: Callable[T]): Future[T] = delegate.submit(MDCCallable(getMDCBag, task))

  def submit[T](task: Runnable, result: T): Future[T] = delegate.submit(MDCRunnable(getMDCBag, task), result)

  def submit(task: Runnable): Future[_] = delegate.submit(MDCRunnable(getMDCBag, task))

  def invokeAll[T](tasks: java.util.Collection[_ <: Callable[T]]): java.util.List[Future[T]] =
    delegate.invokeAll(toMDCTasks(tasks))

  def invokeAll[T](tasks: java.util.Collection[_ <: Callable[T]], timeout: Long, unit: TimeUnit): java.util.List[Future[T]] =
    delegate.invokeAll(toMDCTasks(tasks), timeout, unit)

  def invokeAny[T](tasks: java.util.Collection[_ <: Callable[T]]): T =
    delegate.invokeAny(toMDCTasks(tasks))

  def invokeAny[T](tasks: java.util.Collection[_ <: Callable[T]], timeout: Long, unit: TimeUnit): T =
    delegate.invokeAny(toMDCTasks(tasks), timeout, unit)


  override def prepare(): ExecutionContext = MDCExecutionContextExecutorService(delegate, MDCBag(Option(mongo.getMDCSessionId), mongo.getMDCProduct, mongo.getMDCGroupId))

  protected class MDCCallable[A](sessionId: String, productOpt: Option[String], groupdIdOpt: Option[String], delegate: Callable[A]) extends Callable[A] {
    def call(): A = withMDC(sessionId, productOpt, groupdIdOpt) { delegate.call() }
  }

  protected object MDCCallable {
    def apply[A](sessionId: String, productOpt: Option[String], groupdIdOpt: Option[String], delegate: Callable[A]): Callable[A] = new MDCCallable[A](sessionId, productOpt, groupdIdOpt, delegate)
    def apply[A](mdcBag: MDCBag, delegate: Callable[A]): Callable[A] = {
      mdcBag.sessionId match {
        case Some(sessionId) => apply(sessionId, mdcBag.productOpt, mdcBag.groupIdOpt, delegate)
        case None => delegate
      }
    }
  }

  protected def toMDCTasks[T](tasks: java.util.Collection[_ <: Callable[T]]): java.util.Collection[_ <: Callable[T]] = {
    val mdc = getMDCBag
    tasks.asScala.map(a => MDCCallable(mdc, a)).asJavaCollection
  }
}

object MDCExecutionContextExecutorService {
  def apply(delegate: ExecutionContextExecutorService, preparedMDCBag: MDCBag): MDCExecutionContextExecutorService =
    new MDCExecutionContextExecutorService(delegate, preparedMDCBag)

  def apply(delegate: ExecutionContextExecutorService): MDCExecutionContextExecutorService = apply(delegate, MDCBag(Option(mongo.getMDCSessionId), mongo.getMDCProduct, mongo.getMDCGroupId))
}

class BasicThreadFactory (poolName: String) extends ThreadFactory {
  private val counter = new AtomicInteger(1)
  private def createThreadName = poolName + "-" + counter.getAndIncrement
  def newThread(r: Runnable): Thread = new Thread(r, createThreadName)
}

// TODO - product and group id to become part of monitoring instance passed wholesale
case class MDCBag(sessionId: Option[String], productOpt: Option[String], groupIdOpt: Option[String])
