package com.cognitivecreations.dao.mongo

import com.cognitivecreations.dao.mongo.collection.CollectionOps
import com.cognitivecreations.dao.mongo.sugar._

import scala.TraversableOnce
import scala.collection.generic.CanBuildFrom
import scala.collection._
import scala.concurrent.Future
import scala.util.Random


package object collection {

  implicit class Collection_PimpMyOption[T](val self: Option[T]) extends AnyVal {
    /** @return the instance if Some otherwise if None, die */
    @inline def getOrDie(message : => String) = self.getOrElse(die(message))
  }

  implicit class Collection_PimpMyGentraversableM[A, R, M[AA, RR] <: GenTraversableLike[AA,RR]](val self: M[A,R]) extends AnyVal {
    @inline def tailOption : Option[R] = if(self.nonEmpty) Some(self.tail) else None
  }

  implicit class Collection_PimpMyGenTraversable[A](val self: GenTraversable[A]) extends AnyVal {
    /** @return an iterator of all continuous runs */
    @inline def runs(f: (A,A) => Boolean) : Iterator[GenTraversable[A]] = CollectionOps.runs(self, f)
  }

  implicit class Collection_PimpMyGenTraversableOnce[A, Repr](val self: GenTraversableLike[A, Repr]) extends AnyVal {
    @inline def maxOption[B >: A](implicit cmp: Ordering[B]): Option[A] = {
      if(self.isEmpty) {
        None
      } else {
        Some(self.max[B])
      }
    }
    @inline def minOption[B >: A](implicit cmp: Ordering[B]): Option[A] = {
      if(self.isEmpty) {
        None
      } else {
        Some(self.min[B])
      }
    }
    @inline def maxByOption[B](f: A => B)(implicit cmp: Ordering[B]): Option[A] = {
      if(self.isEmpty) {
        None
      } else {
        Some(self.maxBy(f))
      }
    }
    @inline def minByOption[B](f: A => B)(implicit cmp: Ordering[B]): Option[A] = {
      if(self.isEmpty) {
        None
      } else {
        Some(self.minBy(f))
      }
    }
    /** @return collection with items filtered */
    @inline def filterBy[B, That](f: A => B)(g: B => Boolean)(implicit cbf: CanBuildFrom[Repr, A, That]) : That = {
      // Note: can't use this compiler bug scala 2.10.2
      // self.collect { case a if g(f(a)) => a }
      val builder = cbf()
      self.foreach { a =>
        if(g(f(a))) {
          builder += a
        }
      }
      builder.result()
    }

    @inline def sumBy[B](f: A => B)(implicit num: Numeric[B]): B = {
      var sum = num.zero
      self.foreach { a =>
        sum = num.plus(sum,f(a))
      }
      sum
    }

    @inline def toMapWithKey[K](key: A => K) : immutable.Map[K, A] = {
      val builder = immutable.Map.newBuilder[K,A]
      self.foreach{ a =>
        builder += ( (key(a),a) )
      }
      builder.result()
    }
    @inline def toMapWithMultiKey[K](keygen: A => Traversable[K]) : immutable.Map[K, A] = {
      val builder = immutable.Map.newBuilder[K,A]
      self.foreach{ a =>
        keygen(a).foreach { key =>
          builder += ( (key,a) )
        }
      }
      builder.result()
    }
  }

  implicit class Collection_PimpMyIterator[A](val self: Iterator[A]) extends AnyVal {
    /** @return an iterator of all continuous runs */
    @inline def runs(f: (A,A) => Boolean) : Iterator[Iterator[A]] = CollectionOps.runs(self, f)
  }

  implicit class Collection_PimpMyIndexedSeq[A, M[AA] <: IndexedSeq[AA]](val self: M[A]) extends AnyVal {
    /** @return a copy of the collection with the element at the index removed */
    @inline def remove(n: Int)(implicit cbf: CanBuildFrom[M[A],A,M[A]]): M[A] = CollectionOps.remove(self, n)
  }

  implicit class Collection_PimpMySeq[A, M[AA] <: Seq[AA]](val self: M[A]) extends AnyVal {
    /** @return the element at the specified index or None if index is invalid */
    @inline def get(n: Int): Option[A] = CollectionOps.get(self, n)

    /** @return a random element */
    @inline def random(implicit r: Random) : A = self(r.nextInt(self.size))

    /** @return the collection wrapped in an Option. If the collection is empty then None is returned */
    @inline def wrapOption : Option[M[A]] = if(self.nonEmpty) Some(self) else None

    /**
     *
     * @param filter function used to find the desired element
     * @tparam A
     * @return a pair tuple containing the first match for filter, and the rest of the collection with
     *         the matched element removed
     */
    @inline def removeFirst(filter: (A) => Boolean): (Option[A], Seq[A]) = CollectionOps.removeFirst(self)(filter)
  }

  implicit class Collection_PimpMyTraversableOnce[A, CC[X] <: TraversableOnce[X]](val self: CC[A]) extends AnyVal {
    @inline def shuffle(implicit bf: CanBuildFrom[CC[A], A, CC[A]]): CC[A] = Random.shuffle(self)

    def zipMap[B](f: A => B)(implicit cbf: CanBuildFrom[CC[A], (A,B), CC[(A,B)]]) : CC[(A,B)] = {
      val builder = cbf(self)
      self.foreach { a =>
        builder.+=((a,f(a)))
      }
      builder.result()
    }

    def zipWithIndexMap[B](f: (A,Int) => B)(implicit cbf: CanBuildFrom[CC[A], B, CC[B]]): CC[B] = {
      val b = cbf(self)
      var i = 0
      self.foreach { a =>
        b += f(a, i)
        i += 1
      }
      b.result()
    }

  }

  implicit class Collection_PimpMyMapLike[A, B, MM[AA,BB] <: GenMap[AA,BB]](val self: MM[A,B]) extends AnyVal {
    @inline def liftFut : A => Future[Option[B]] = { a:A => Future.successful(self.get(a)) }
  }

}
