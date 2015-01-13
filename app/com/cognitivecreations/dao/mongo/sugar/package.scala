package com.cognitivecreations.dao.mongo

import com.cognitivecreations.dao.mongo.sugar._
import com.cognitivecreations.dao.mongo.sugar.CallOnce1

package object sugar extends Logging {
  type =>?:[-A,+B] = PartialFunction[A,B]
  type =>![-A,+R] = CallOnce1[A,R]

  /** @return kill the application by throwing an unchecked exception and logging the error */
  @inline def die(message: String) = {
    error(message)
    throw new RuntimeException(message)
  }

  /** @return kill the application by throwing an unchecked exception and logging the error */
  @inline def die(throwable : Throwable) = {
    error(throwable)
    throw throwable
  }

  implicit class Sugar_PimpEverything[A](val self: A) extends AnyVal {
    @inline def morph[B](f: A => B) : B = f(self)
    @inline def morphIf(test: A => Boolean)(f: A => A) : A = if(test(self)) f(self) else self
    @inline def morphIfOrElse[B](test: A => Boolean, orElse: A => B)(f: A => B) : B = if(test(self)) f(self) else orElse(self)
    @inline def morphIfOrDefault[B](test: A => Boolean, default: => B)(f: A => B) : B = if(test(self)) f(self) else default
    @inline def compareOrder(other: A)(implicit o:Ordering[A]) = o.compare(self, other)
    @inline def kestrel(f: A => Any) = { f(self); self }
    @inline def tap(f: A => Any) = kestrel(f)
    @inline def some: Option[A] = Some(self)
  }

  implicit class Sugar_PimpMyBoolean(val self: Boolean) extends AnyVal {
    def toInt : Int = if(self) 1 else 0
    def opt[A](value: => A): Option[A] = if(self) Some(value) else None
    def fold[A](trueFn: => A, falseFn: => A) = if(self) trueFn else falseFn
  }

  implicit class Sugar_PimpMyInt(val self: Int) extends AnyVal {
    // Used to chain compare checks
    @inline def orIfEqual(alternative: => Int): Int =
      if(self == 0) alternative else self
  }

  implicit class Sugar_PimpMyFunction[A](val self: () => A) extends AnyVal {
    @inline def toRunnable = new Runnable {
      override def run(): Unit = self()
    }
  }

  implicit class Sugar_EnrichMyOption[A](val self: Option[A]) extends AnyVal {
    @inline def tapNone[U](fn: => U) : Option[A] = {
      if(self.isEmpty) fn
      self
    }
  }

}
