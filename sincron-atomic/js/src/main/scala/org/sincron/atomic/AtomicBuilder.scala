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

trait AtomicBuilder[T] extends Serializable {
  type R <: Atomic[T]

  def buildInstance(initialValue: T, strategy: PaddingStrategy): R
}

private[atomic] object Implicits {
  abstract class Level1 {
    implicit def AtomicRefBuilder[T <: AnyRef]: AtomicBuilder[T] =
      new AtomicBuilder[T] {
        type R = AtomicAny[T]

        def buildInstance(initialValue: T, strategy: PaddingStrategy): AtomicAny[T] =
          AtomicAny(initialValue)
      }
  }

  abstract class Level2 extends Level1 {
    implicit def AtomicNumberBuilder[T  <: AnyRef : Numeric]: AtomicBuilder[T] =
      new AtomicBuilder[T] {
        type R = AtomicNumberAny[T]

        def buildInstance(initialValue: T, strategy: PaddingStrategy) =
          AtomicNumberAny(initialValue)
      }
  }
}

object AtomicBuilder extends Implicits.Level2 {
  implicit val AtomicIntBuilder: AtomicBuilder[Int] =
    new AtomicBuilder[Int] {
      type R = AtomicInt

      def buildInstance(initialValue: Int, strategy: PaddingStrategy) =
        AtomicInt(initialValue)
    }

  implicit val AtomicLongBuilder: AtomicBuilder[Long] =
    new AtomicBuilder[Long] {
      type R = AtomicLong

      def buildInstance(initialValue: Long, strategy: PaddingStrategy) =
        AtomicLong(initialValue)
    }

  implicit val AtomicBooleanBuilder: AtomicBuilder[Boolean] =
    new AtomicBuilder[Boolean] {
      type R = AtomicBoolean

      def buildInstance(initialValue: Boolean, strategy: PaddingStrategy) =
        AtomicBoolean(initialValue)
    }

  implicit val AtomicByteBuilder: AtomicBuilder[Byte] =
    new AtomicBuilder[Byte] {
      type R = AtomicByte

      def buildInstance(initialValue: Byte, strategy: PaddingStrategy): AtomicByte =
        AtomicByte(initialValue)
    }

  implicit val AtomicCharBuilder: AtomicBuilder[Char] =
    new AtomicBuilder[Char] {
      type R = AtomicChar

      def buildInstance(initialValue: Char, strategy: PaddingStrategy): AtomicChar =
        AtomicChar(initialValue)
    }

  implicit val AtomicShortBuilder: AtomicBuilder[Short] =
    new AtomicBuilder[Short] {
      type R = AtomicShort

      def buildInstance(initialValue: Short, strategy: PaddingStrategy): AtomicShort =
        AtomicShort(initialValue)
    }

  implicit val AtomicFloatBuilder: AtomicBuilder[Float] =
    new AtomicBuilder[Float] {
      type R = AtomicFloat

      def buildInstance(initialValue: Float, strategy: PaddingStrategy): AtomicFloat =
        AtomicFloat(initialValue)
    }

  implicit val AtomicDoubleBuilder: AtomicBuilder[Double] =
    new AtomicBuilder[Double] {
      type R = AtomicDouble

      def buildInstance(initialValue: Double, strategy: PaddingStrategy): AtomicDouble =
        AtomicDouble(initialValue)
    }
}
