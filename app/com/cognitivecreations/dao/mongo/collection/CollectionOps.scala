package com.cognitivecreations.dao.mongo.collection

import scala.annotation.tailrec
import scala.collection.generic.CanBuildFrom
import scala.collection.{Seq, IndexedSeq, GenTraversable, Iterator}

object CollectionOps {
  def runs[A](self: Iterator[A], f: (A,A) => Boolean) : Iterator[Iterator[A]] = new Iterator[Iterator[A]] {
    val i = self.buffered
    def hasNext = i.hasNext
    def next() = new Iterator[A] {
      val headValue = i.head
      def hasNext = i.hasNext && f(headValue, i.head)
      def next() = i.next()
    }
  }

  def runs[A](self: GenTraversable[A], f: (A,A) => Boolean) : Iterator[GenTraversable[A]] = new Iterator[GenTraversable[A]] {
    var xs = self
    def hasNext = xs.nonEmpty
    def next() = {
      val headValue = xs.head
      val headRun = xs.takeWhile(f(headValue,_))
      xs = xs.slice(headRun.size, xs.size)
      headRun
    }
  }

  def remove[A, M[AA] <: IndexedSeq[AA]](self: IndexedSeq[A], n: Int)(implicit cbf: CanBuildFrom[M[A],A,M[A]]): M[A] = {
    val b = cbf()
    b.sizeHint(self.size)
    var i = 0
    self.foreach{ x => if (i != n) { b += x }; i += 1 }
    b.result()
  }

  //TODO: make this work with more generic collection types
  def removeFirst[A](self: Seq[A])(filter: (A) => Boolean): (Option[A], Seq[A]) = {

    @tailrec
    def step(remaining: Seq[A], used: Seq[A] = Nil): (Option[A], Seq[A]) =
      remaining match {
        case x :: xs if filter(x) => Some(x) -> (used ++ xs)
        case x :: xs => step(xs, used :+ x)
        case Nil => None -> (used ++ remaining)
      }

    step(self)
  }

  def get[A, M[AA] <: Seq[AA]](self: Seq[A], n: Int) : Option[A] = {
    if(n < self.size) {
      Some(self(n))
    } else {
      None
    }
  }
}
