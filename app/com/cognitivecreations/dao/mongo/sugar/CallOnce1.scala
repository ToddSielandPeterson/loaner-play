package com.cognitivecreations.dao.mongo.sugar

class CallOnce1[-A,+R](f: A => R) extends (A => R) {
  private val lock = new Object
  private var called = false
  def canCall = lock.synchronized { called }
  def apply(a: A) : R = {
    lock.synchronized {
      if(called == false) {
        called = true
      } else {
        throw new IllegalStateException()
      }
    }
    // Execute f(a) outside sync block
    // Note: can't reach here if called was set to true
    f(a)
  }
}
