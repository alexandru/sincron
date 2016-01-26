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

trait AtomicBuilder[T, R <: Atomic[T]] {
  def buildInstance(initialValue: T, strategy: PaddingStrategy): R
}

private[atomic] object Implicits {
  abstract class Level1 {
    implicit def AtomicRefBuilder[T <: AnyRef] = new AtomicBuilder[T, AtomicAny[T]] {
      def buildInstance(initialValue: T, strategy: PaddingStrategy) =
        AtomicAny(initialValue)(strategy)
    }
  }

  abstract class Level2 extends Level1 {
    implicit def AtomicNumberBuilder[T <: AnyRef : Numeric] =
      new AtomicBuilder[T, AtomicNumberAny[T]] {
        def buildInstance(initialValue: T, strategy: PaddingStrategy) =
          AtomicNumberAny(initialValue)(implicitly[Numeric[T]], strategy)
      }
  }
}

object AtomicBuilder extends Implicits.Level2 {
  implicit object AtomicIntBuilder extends AtomicBuilder[Int, AtomicInt] {
    def buildInstance(initialValue: Int, strategy: PaddingStrategy): AtomicInt =
      AtomicInt(initialValue)(strategy)
  }

  implicit object AtomicLongBuilder extends AtomicBuilder[Long, AtomicLong] {
    def buildInstance(initialValue: Long, strategy: PaddingStrategy): AtomicLong =
      AtomicLong(initialValue)(strategy)
  }

  implicit object AtomicBooleanBuilder extends AtomicBuilder[Boolean, AtomicBoolean] {
    def buildInstance(initialValue: Boolean, strategy: PaddingStrategy) =
      AtomicBoolean(initialValue)(strategy)
  }

  implicit object AtomicByteBuilder extends AtomicBuilder[Byte, AtomicByte] {
    def buildInstance(initialValue: Byte, strategy: PaddingStrategy): AtomicByte =
      AtomicByte(initialValue)(strategy)
  }

  implicit object AtomicCharBuilder extends AtomicBuilder[Char, AtomicChar] {
    def buildInstance(initialValue: Char, strategy: PaddingStrategy): AtomicChar =
      AtomicChar(initialValue)(strategy)
  }

  implicit object AtomicShortBuilder extends AtomicBuilder[Short, AtomicShort] {
    def buildInstance(initialValue: Short, strategy: PaddingStrategy): AtomicShort =
      AtomicShort(initialValue)(strategy)
  }

  implicit object AtomicFloatBuilder extends AtomicBuilder[Float, AtomicFloat] {
    def buildInstance(initialValue: Float, strategy: PaddingStrategy): AtomicFloat =
      AtomicFloat(initialValue)(strategy)
  }

  implicit object AtomicDoubleBuilder extends AtomicBuilder[Double, AtomicDouble] {
    def buildInstance(initialValue: Double, strategy: PaddingStrategy): AtomicDouble =
      AtomicDouble(initialValue)(strategy)
  }
}
