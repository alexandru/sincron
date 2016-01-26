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
  def buildInstance(initialValue: T): R
}

private[atomic] object Implicits {
  abstract class Level1 {
    implicit def AtomicRefBuilder[T] = new AtomicBuilder[T, AtomicAny[T]] {
      def buildInstance(initialValue: T) =
        AtomicAny(initialValue)
    }
  }

  abstract class Level2 extends Level1 {
    implicit def AtomicNumberBuilder[T : Numeric] =
      new AtomicBuilder[T, AtomicNumberAny[T]] {
        def buildInstance(initialValue: T) =
          AtomicNumberAny(initialValue)
      }
  }
}

object AtomicBuilder extends Implicits.Level2 {
  implicit object AtomicIntBuilder extends AtomicBuilder[Int, AtomicInt] {
    def buildInstance(initialValue: Int): AtomicInt =
      AtomicInt(initialValue)
  }

  implicit object AtomicLongBuilder extends AtomicBuilder[Long, AtomicLong] {
    def buildInstance(initialValue: Long): AtomicLong =
      AtomicLong(initialValue)
  }

  implicit object AtomicBooleanBuilder extends AtomicBuilder[Boolean, AtomicBoolean] {
    def buildInstance(initialValue: Boolean) =
      AtomicBoolean(initialValue)
  }

  implicit object AtomicByteBuilder extends AtomicBuilder[Byte, AtomicByte] {
    def buildInstance(initialValue: Byte): AtomicByte =
      AtomicByte(initialValue)
  }

  implicit object AtomicCharBuilder extends AtomicBuilder[Char, AtomicChar] {
    def buildInstance(initialValue: Char): AtomicChar =
      AtomicChar(initialValue)
  }

  implicit object AtomicShortBuilder extends AtomicBuilder[Short, AtomicShort] {
    def buildInstance(initialValue: Short): AtomicShort =
      AtomicShort(initialValue)
  }

  implicit object AtomicFloatBuilder extends AtomicBuilder[Float, AtomicFloat] {
    def buildInstance(initialValue: Float): AtomicFloat =
      AtomicFloat(initialValue)
  }

  implicit object AtomicDoubleBuilder extends AtomicBuilder[Double, AtomicDouble] {
    def buildInstance(initialValue: Double): AtomicDouble =
      AtomicDouble(initialValue)
  }
}
