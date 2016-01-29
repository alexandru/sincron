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

package org.sincron.atomic.inline

import org.sincron.atomic.PaddingStrategy.NoPadding
import org.sincron.atomic.{PaddingStrategy, AtomicBuilder, Atomic}
import org.sincron.atomic.inline.compat._

object AtomicMacros {
  def transformMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })
    (cb: c.Expr[T => T]): c.Expr[Unit] = {

    import c.universe._
    val util = SyntaxUtil[c.type](c)
    val selfExpr: c.Expr[Atomic[T]] = c.prefix

    val self = util.name("self")
    val current = util.name("current")
    val update = util.name("update")

    val tree =
      q"""
        val $self = $selfExpr
        var $current = $self.get
        var $update = $cb($current)

        while (!$self.compareAndSet($current, $update)) {
          $current = $self.get
          $update = $cb($current)
        }
      """

    new InlineUtil[c.type](c).inlineAndReset[Unit](tree)
  }

  def transformAndGetMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })
    (cb: c.Expr[T => T]): c.Expr[T] = {

    import c.universe._
    val util = SyntaxUtil[c.type](c)
    val selfExpr: c.Expr[Atomic[T]] = c.prefix

    val self = util.name("self")
    val current = util.name("current")
    val update = util.name("update")

    val tree =
      q"""
        val $self = $selfExpr
        var $current = $self.get
        var $update = $cb($current)

        while (!$self.compareAndSet($current, $update)) {
          $current = $self.get
          $update = $cb($current)
        }

        $update
      """

    new InlineUtil[c.type](c).inlineAndReset[T](tree)
  }

  def getAndTransformMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })
    (cb: c.Expr[T => T]): c.Expr[T] = {

    import c.universe._
    val util = SyntaxUtil[c.type](c)
    val selfExpr: c.Expr[Atomic[T]] = c.prefix

    val self = util.name("self")
    val current = util.name("current")
    val update = util.name("update")

    val tree =
      q"""
        val $self = $selfExpr
        var $current = $self.get
        var $update = $cb($current)

        while (!$self.compareAndSet($current, $update)) {
          $current = $self.get
          $update = $cb($current)
        }

        $current
      """

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

    val tree =
      q"""
        val $self = $selfExpr
        var $current = $self.get
        var ($resultVar, $updateVar) = $cb($current)

        while (!$self.compareAndSet($current, $updateVar)) {
          $current = $self.get
          val ($resultTmp, $updateTmp) = $cb($current)
          $updateVar = $updateTmp
          $resultVar = $resultTmp
        }

        $resultVar
      """

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
}
