/*
 * Copyright (c) 2016 by its authors. Some rights reserved.
 * See the project homepage at: https://sincron.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sincron.atomic

import org.sincron.atomic.PaddingStrategy.NoPadding
import org.sincron.macros.compat._
import org.sincron.macros.{InlineUtil, SyntaxUtil, compat}
import scala.language.experimental.macros

/**
  * Base trait of all atomic references, no matter the type.
  */
abstract class Atomic[T] {
  /** Get the current value persisted by this Atomic. */
  def get: T

  /** Get the current value persisted by this Atomic, an alias for `get()`. */
  final def apply(): T = macro Atomic.Macros.applyMacro[T]

  /** Updates the current value.
    *
    * @param update will be the new value returned by `get()`
    */
  def set(update: T): Unit

  /** Alias for [[set]]. Updates the current value.
    *
    * @param value will be the new value returned by `get()`
    */
  final def update(value: T): Unit = macro Atomic.Macros.setMacro[T]

  /** Alias for [[set]]. Updates the current value.
    *
    * @param value will be the new value returned by `get()`
    */
  final def `:=`(value: T): Unit = macro Atomic.Macros.setMacro[T]

  /** Does a compare-and-set operation on the current value. For more info, checkout the related
    * [[https://en.wikipedia.org/wiki/Compare-and-swap Compare-and-swap Wikipedia page]].
    *
    * It's an atomic, worry free operation.
    *
    * @param expect is the value you expect to be persisted when the operation happens
    * @param update will be the new value, should the check for `expect` succeeds
    * @return either true in case the operation succeeded or false otherwise
    */
  def compareAndSet(expect: T, update: T): Boolean

  /** Sets the persisted value to `update` and returns the old value that was in place.
    * It's an atomic, worry free operation.
    */
  def getAndSet(update: T): T

  /** Eventually sets to the given value.
    * Has weaker visibility guarantees than the normal `set()`.
    */
  def lazySet(update: T): Unit

  /** Abstracts over `compareAndSet`. You specify a transformation by specifying a callback to be
    * executed, a callback that transforms the current value. This method will loop until it will
    * succeed in replacing the current value with the one produced by your callback.
    *
    * Note that the callback will be executed on each iteration of the loop, so it can be called
    * multiple times - don't do destructive I/O or operations that mutate global state in it.
    *
    * @param cb is a callback that receives the current value as input and returns a tuple that specifies
    *           the update + what should this method return when the operation succeeds.
    * @return whatever was specified by your callback, once the operation succeeds
    */
  final def transformAndExtract[U](cb: (T) => (U, T)): U =
    macro Atomic.Macros.transformAndExtractMacro[T, U]

  /** Abstracts over `compareAndSet`. You specify a transformation by specifying a callback to be
    * executed, a callback that transforms the current value. This method will loop until it will
    * succeed in replacing the current value with the one produced by the given callback.
    *
    * Note that the callback will be executed on each iteration of the loop, so it can be called
    * multiple times - don't do destructive I/O or operations that mutate global state in it.
    *
    * @param cb is a callback that receives the current value as input and returns the `update` which is the
    *           new value that should be persisted
    * @return whatever the update is, after the operation succeeds
    */
  final def transformAndGet(cb: (T) => T): T =
    macro Atomic.Macros.transformAndGetMacro[T]

  /** Abstracts over `compareAndSet`. You specify a transformation by specifying a callback to be
    * executed, a callback that transforms the current value. This method will loop until it will
    * succeed in replacing the current value with the one produced by the given callback.
    *
    * Note that the callback will be executed on each iteration of the loop, so it can be called
    * multiple times - don't do destructive I/O or operations that mutate global state in it.
    *
    * @param cb is a callback that receives the current value as input and returns the `update` which is the
    *           new value that should be persisted
    * @return the old value, just prior to when the successful update happened
    */
  final def getAndTransform(cb: (T) => T): T =
    macro Atomic.Macros.getAndTransformMacro[T]

  /** Abstracts over `compareAndSet`. You specify a transformation by specifying a callback to be
    * executed, a callback that transforms the current value. This method will loop until it will
    * succeed in replacing the current value with the one produced by the given callback.
    *
    * Note that the callback will be executed on each iteration of the loop, so it can be called
    * multiple times - don't do destructive I/O or operations that mutate global state in it.
    *
    * @param cb is a callback that receives the current value as input and returns the `update` which is the
    *           new value that should be persisted
    */
  final def transform(cb: (T) => T): Unit =
    macro Atomic.Macros.transformMacro[T]
}

object Atomic {
  /** Constructs an `Atomic[T]` reference. Based on the `initialValue`, it will return the best, most specific
    * type. E.g. you give it a number, it will return something inheriting from `AtomicNumber[T]`. That's why
    * it takes an `AtomicBuilder[T, R]` as an implicit parameter - but worry not about such details as it just works.
    *
    * @param initialValue is the initial value with which to initialize the Atomic reference
    * @param builder is the builder that helps us to build the best reference possible, based on our `initialValue`
    */
  def apply[T, R <: Atomic[T]](initialValue: T)(implicit builder: AtomicBuilder[T, R]): R =
    macro Atomic.Macros.buildAnyMacro[T, R]

  /** Constructs an `Atomic[T]` reference. Based on the `initialValue`, it will return the best, most specific
    * type. E.g. you give it a number, it will return something inheriting from `AtomicNumber[T]`. That's why
    * it takes an `AtomicBuilder[T, R]` as an implicit parameter - but worry not about such details as it just works.
    *
    * @param initialValue is the initial value with which to initialize the Atomic reference
    * @param padding is the [[PaddingStrategy]] to apply
    * @param builder is the builder that helps us to build the best reference possible, based on our `initialValue`
    */
  def withPadding[T, R <: Atomic[T]](initialValue: T, padding: PaddingStrategy)(implicit builder: AtomicBuilder[T, R]): R =
    macro Atomic.Macros.buildAnyWithPaddingMacro[T, R]

  /** Returns the builder that would be chosen to construct Atomic references
    * for the given `initialValue`.
    */
  def builderFor[T, R <: Atomic[T]](initialValue: T)(implicit builder: AtomicBuilder[T, R]): AtomicBuilder[T, R] =
    builder

  /** Macros implementations for the [[Atomic]] type */
  object Macros {
    def transformMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })
      (cb: c.Expr[T => T]): c.Expr[Unit] = {

      import c.universe._
      val util = SyntaxUtil[c.type](c)
      val selfExpr: c.Expr[Atomic[T]] = c.prefix

      val current = util.name("current")
      val update = util.name("update")

      /* If our arguments are all clean (stable identifiers or simple functions)
       * then inline them directly, otherwise bind arguments to a val for safety.
       */
      val tree =
        if (util.isClean(selfExpr, cb)) {
          q"""
          var $current = $selfExpr.get
          var $update = $cb($current)

          while (!$selfExpr.compareAndSet($current, $update)) {
            $current = $selfExpr.get
            $update = $cb($current)
          }
          """
        } else {
          val self = util.name("self")
          val fn = util.name("fn")
          q"""
          val $self = $selfExpr
          val $fn = $cb

          var $current = $self.get
          var $update = $fn($current)

          while (!$self.compareAndSet($current, $update)) {
            $current = $self.get
            $update = $fn($current)
          }
          """
        }


      new InlineUtil[c.type](c).inlineAndReset[Unit](tree)
    }

    def transformAndGetMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })
      (cb: c.Expr[T => T]): c.Expr[T] = {

      import c.universe._
      val util = SyntaxUtil[c.type](c)
      val selfExpr: c.Expr[Atomic[T]] = c.prefix

      val current = util.name("current")
      val update = util.name("update")

      /* If our arguments are all clean (stable identifiers or simple functions)
       * then inline them directly, otherwise bind arguments to a val for safety.
       */
      val tree =
        if (util.isClean(selfExpr, cb)) {
          q"""
          var $current = $selfExpr.get
          var $update = $cb($current)

          while (!$selfExpr.compareAndSet($current, $update)) {
            $current = $selfExpr.get
            $update = $cb($current)
          }

          $update
          """
        } else {
          val self = util.name("self")
          val fn = util.name("fn")
          q"""
          val $self = $selfExpr
          val $fn = $cb
          var $current = $self.get
          var $update = $fn($current)

          while (!$self.compareAndSet($current, $update)) {
            $current = $self.get
            $update = $fn($current)
          }

          $update
          """
        }


      new InlineUtil[c.type](c).inlineAndReset[T](tree)
    }

    def getAndTransformMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })
      (cb: c.Expr[T => T]): c.Expr[T] = {

      import c.universe._
      val util = SyntaxUtil[c.type](c)
      val selfExpr: c.Expr[Atomic[T]] = c.prefix

      val current = util.name("current")
      val update = util.name("update")

      /* If our arguments are all clean (stable identifiers or simple functions)
       * then inline them directly, otherwise bind arguments to a val for safety.
       */
      val tree =
        if (util.isClean(selfExpr, cb)) {
          q"""
          var $current = $selfExpr.get
          var $update = $cb($current)

          while (!$selfExpr.compareAndSet($current, $update)) {
            $current = $selfExpr.get
            $update = $cb($current)
          }

          $current
          """
        } else {
          val self = util.name("self")
          val fn = util.name("fn")
          q"""
          val $self = $selfExpr
          val $fn = $cb
          var $current = $self.get
          var $update = $fn($current)

          while (!$self.compareAndSet($current, $update)) {
            $current = $self.get
            $update = $fn($current)
          }

          $current
          """
        }

      new InlineUtil[c.type](c).inlineAndReset[T](tree)
    }

    def transformAndExtractMacro[S : c.WeakTypeTag, A : c.WeakTypeTag]
    (c: Context { type PrefixType = Atomic[S] })
      (cb: c.Expr[S => (A, S)]): c.Expr[A] = {

      import c.universe._
      val util = SyntaxUtil[c.type](c)
      val selfExpr: c.Expr[Atomic[S]] = c.prefix

      val self = util.name("self")
      val current = util.name("current")
      val updateVar = util.name("updateVar")
      val resultVar = util.name("resultVar")
      val updateTmp = util.name("updateTmp")
      val resultTmp = util.name("resultTmp")

      /* If our arguments are all clean (stable identifiers or simple functions)
       * then inline them directly, otherwise bind arguments to a val for safety.
       */
      val tree =
        if (util.isClean(selfExpr, cb)) {
          q"""
          var $current = $selfExpr.get
          var ($resultVar, $updateVar) = $cb($current)

          while (!$selfExpr.compareAndSet($current, $updateVar)) {
            $current = $selfExpr.get
            val ($resultTmp, $updateTmp) = $cb($current)
            $updateVar = $updateTmp
            $resultVar = $resultTmp
          }

          $resultVar
          """
        } else {
          val self = util.name("self")
          val fn = util.name("fn")
          q"""
          val $self = $selfExpr
          val $fn = $cb

          var $current = $self.get
          var ($resultVar, $updateVar) = $fn($current)

          while (!$self.compareAndSet($current, $updateVar)) {
            $current = $self.get
            val ($resultTmp, $updateTmp) = $fn($current)
            $updateVar = $updateTmp
            $resultVar = $resultTmp
          }

          $resultVar
          """
        }

      new InlineUtil[c.type](c).inlineAndReset[A](tree)
    }

    def buildAnyMacro[T : c.WeakTypeTag, R <: Atomic[T] : c.WeakTypeTag](c: Context)
      (initialValue: c.Expr[T])
      (builder: c.Expr[AtomicBuilder[T, R]]): c.Expr[R] = {

      import c.universe._
      val expr = reify {
        builder.splice.buildInstance(initialValue.splice, NoPadding)
      }

      new InlineUtil[c.type](c).inlineAndReset[R](expr.tree)
    }

    def buildAnyWithPaddingMacro[T : c.WeakTypeTag, R <: Atomic[T] : c.WeakTypeTag](c: Context)
      (initialValue: c.Expr[T], padding: c.Expr[PaddingStrategy])
      (builder: c.Expr[AtomicBuilder[T, R]]): c.Expr[R] = {

      import c.universe._
      val expr = reify {
        builder.splice.buildInstance(initialValue.splice, padding.splice)
      }

      new InlineUtil[c.type](c).inlineAndReset[R](expr.tree)
    }

    def applyMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })(): c.Expr[T] = {
      import c.universe._
      val selfExpr: c.Expr[Atomic[T]] = c.prefix
      val tree = q"""$selfExpr.get"""
      new InlineUtil[c.type](c).inlineAndReset[T](tree)
    }

    def setMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })
      (value: c.Expr[T]): c.Expr[Unit] = {
      import c.universe._
      val selfExpr: c.Expr[Atomic[T]] = c.prefix
      val tree = q"""$selfExpr.set($value)"""
      new InlineUtil[c.type](c).inlineAndReset[Unit](tree)
    }

    def addMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })
      (value: c.Expr[T]): c.Expr[Unit] = {
      import c.universe._
      val selfExpr: c.Expr[Atomic[T]] = c.prefix
      val tree = q"""$selfExpr.add($value)"""
      new InlineUtil[c.type](c).inlineAndReset[Unit](tree)
    }

    def subtractMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })
      (value: c.Expr[T]): c.Expr[Unit] = {
      import c.universe._
      val selfExpr: c.Expr[Atomic[T]] = c.prefix
      val tree = q"""$selfExpr.subtract($value)"""
      new InlineUtil[c.type](c).inlineAndReset[Unit](tree)
    }
  }
}
