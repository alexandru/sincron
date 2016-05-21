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

import scala.language.experimental.macros
import org.sincron.macros.compat

trait AtomicBuilder[T] extends Serializable {
  type R <: Atomic[T]

  def buildInstance(initialValue: T, strategy: PaddingStrategy): R
}

private[atomic] object Implicits {
  abstract class Level1 {
    implicit def AtomicRefBuilder[T <: AnyRef] = new AtomicBuilder[T] {
      type R = AtomicAny[T]

      def buildInstance(initialValue: T, strategy: PaddingStrategy): AtomicAny[T] =
        AtomicAny(initialValue)(strategy)
    }
  }

  abstract class Level2 extends Level1 {
    implicit def AtomicNumberBuilder[T <: AnyRef : Numeric] =
      new AtomicBuilder[T] {
        type R = AtomicNumberAny[T]

        def buildInstance(initialValue: T, strategy: PaddingStrategy) =
          AtomicNumberAny(initialValue)(implicitly[Numeric[T]], strategy)
      }
  }
}

object AtomicBuilder extends Implicits.Level2 {
  implicit object AtomicIntBuilder extends AtomicBuilder[Int] {
    type R = AtomicInt

    def buildInstance(initialValue: Int, strategy: PaddingStrategy): AtomicInt =
      AtomicInt(initialValue)(strategy)
  }

  implicit object AtomicLongBuilder extends AtomicBuilder[Long] {
    type R = AtomicLong

    def buildInstance(initialValue: Long, strategy: PaddingStrategy): AtomicLong =
      AtomicLong(initialValue)(strategy)
  }

  implicit object AtomicBooleanBuilder extends AtomicBuilder[Boolean] {
    type R = AtomicBoolean

    def buildInstance(initialValue: Boolean, strategy: PaddingStrategy): AtomicBoolean =
      AtomicBoolean(initialValue)(strategy)
  }

  implicit object AtomicByteBuilder extends AtomicBuilder[Byte] {
    type R = AtomicByte

    def buildInstance(initialValue: Byte, strategy: PaddingStrategy): AtomicByte =
      AtomicByte(initialValue)(strategy)
  }

  implicit object AtomicCharBuilder extends AtomicBuilder[Char] {
    type R = AtomicChar

    def buildInstance(initialValue: Char, strategy: PaddingStrategy): AtomicChar =
      AtomicChar(initialValue)(strategy)
  }

  implicit object AtomicShortBuilder extends AtomicBuilder[Short] {
    type R = AtomicShort

    def buildInstance(initialValue: Short, strategy: PaddingStrategy): AtomicShort =
      AtomicShort(initialValue)(strategy)
  }

  implicit object AtomicFloatBuilder extends AtomicBuilder[Float] {
    type R = AtomicFloat

    def buildInstance(initialValue: Float, strategy: PaddingStrategy): AtomicFloat =
      AtomicFloat(initialValue)(strategy)
  }

  implicit object AtomicDoubleBuilder extends AtomicBuilder[Double] {
    type R = AtomicDouble

    def buildInstance(initialValue: Double, strategy: PaddingStrategy): AtomicDouble =
      AtomicDouble(initialValue)(strategy)
  }

  /** Macros implementations for building [[Atomic]] instances. */
  object Macros {
    def buildAnyMacro[T: c.WeakTypeTag](c: compat.Context)
      (initialValue: c.Expr[T])
      (builder: c.Expr[AtomicBuilder[T]]): c.Expr[builder.value.R] = {

      import c.universe._
      c.Expr[builder.value.R](
        q"""
         $builder.buildInstance($initialValue, _root_.org.sincron.atomic.PaddingStrategy.NoPadding)
         """)
    }

    def buildAnyWithPaddingMacro[T: c.WeakTypeTag](c: compat.Context)
      (initialValue: c.Expr[T], padding: c.Expr[PaddingStrategy])
      (builder: c.Expr[AtomicBuilder[T]]): c.Expr[builder.value.R] = {

      import c.universe._
      c.Expr[builder.value.R](
        q"""
        $builder.buildInstance($initialValue, $padding)
        """)
    }

    def builderForSimpleMacro[T: c.WeakTypeTag](c: compat.Context)
      (builder: c.Expr[AtomicBuilder[T]]): c.Expr[AtomicBuilder[T]] = {

      import c.universe._
      c.Expr[AtomicBuilder[T]](
        q"""
        $builder
        """)
    }

    def builderForMacro[T: c.WeakTypeTag](c: compat.Context)
      (initialValue: c.Expr[T])(builder: c.Expr[AtomicBuilder[T]]): c.Expr[AtomicBuilder[T]] = {

      import c.universe._
      c.Expr[AtomicBuilder[T]](
        q"""
        $builder
        """)
    }
  }
}
