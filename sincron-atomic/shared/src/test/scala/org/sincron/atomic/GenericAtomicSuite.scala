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

import minitest.SimpleTestSuite
import org.sincron.atomic.PaddingStrategy._

abstract class GenericAtomicSuite[T, R <: Atomic[T]]
  (builder: AtomicBuilder[T, R], strategy: PaddingStrategy, valueFromInt: Int => T, valueToInt: T => Int)
  extends SimpleTestSuite {

  def Atomic(initial: T): R = builder.buildInstance(initial, strategy)
  val zero = valueFromInt(0)
  val one = valueFromInt(1)
  val two = valueFromInt(2)

  test("should set()") {
    val r = Atomic(zero)
    assert(r.get == zero)
    r.set(one)
    assert(r.get == one)
  }

  test("should getAndSet()") {
    val r = Atomic(zero)
    assert(r.get == zero)
    val old = r.getAndSet(one)
    assert(old == zero)
    assert(r.get == one)
  }

  test("should compareAndSet()") {
    val r = Atomic(zero)
    assert(r.get == zero)

    assert(r.compareAndSet(zero, one))
    assert(r.get == one)
    assert(r.compareAndSet(one, zero))
    assert(r.get == zero)
    assert(!r.compareAndSet(one, one))
    assert(r.get == zero)
  }

  test("should transform") {
    val r = Atomic(zero)
    assert(r.get == zero)

    r.transform(x => valueFromInt(valueToInt(x) + 1))
    assert(r.get == one)
    r.transform(x => valueFromInt(valueToInt(x) + 1))
    assert(r.get == two)
  }

  test("should transformAndGet") {
    val r = Atomic(zero)
    assert(r.get == zero)

    assert(r.transformAndGet(x => valueFromInt(valueToInt(x) + 1)) == one)
    assert(r.transformAndGet(x => valueFromInt(valueToInt(x) + 1)) == two)
    assert(r.get == two)
  }

  test("should getAndTransform") {
    val r = Atomic(zero)
    assert(r.get == zero)

    assert(r.getAndTransform(x => valueFromInt(valueToInt(x) + 1)) == zero)
    assert(r.getAndTransform(x => valueFromInt(valueToInt(x) + 1)) == one)
    assert(r.get == two)
  }

  test("should transformAndExtract") {
    val r = Atomic(zero)
    assert(r.get == zero)

    assert(r.transformAndExtract(x => (x, valueFromInt(valueToInt(x) + 1))) == zero)
    assert(r.transformAndExtract(x => (x, valueFromInt(valueToInt(x) + 1))) == one)
    assert(r.get == two)
  }
}

// -- NoPadding

object GenericAtomicAnyNoPadding extends GenericAtomicSuite[String, AtomicAny[String]](
  Atomic.builderFor(""), NoPadding, x => x.toString, x => x.toInt)

object GenericAtomicBooleanNoPadding extends GenericAtomicSuite[Boolean, AtomicBoolean](
  Atomic.builderFor(true), NoPadding, x => if (x == 1) true else false, x => if (x) 1 else 0)

object GenericAtomicNumberAnyNoPadding extends GenericAtomicSuite[BoxedLong, AtomicNumberAny[BoxedLong]](
  AtomicBuilder.AtomicNumberBuilder[BoxedLong], NoPadding, x => BoxedLong(x), x => x.value.toInt)

object GenericAtomicFloatNoPadding extends GenericAtomicSuite[Float, AtomicFloat](
  Atomic.builderFor(0.0f), NoPadding, x => x.toFloat, x => x.toInt)

object GenericAtomicDoubleNoPadding extends GenericAtomicSuite[Double, AtomicDouble](
  Atomic.builderFor(0.toDouble), NoPadding, x => x.toDouble, x => x.toInt)

object GenericAtomicShortNoPadding extends GenericAtomicSuite[Short, AtomicShort](
  Atomic.builderFor(0.toShort), NoPadding, x => x.toShort, x => x.toInt)

object GenericAtomicByteNoPadding extends GenericAtomicSuite[Byte, AtomicByte](
  Atomic.builderFor(0.toByte), NoPadding, x => x.toByte, x => x.toInt)

object GenericAtomicCharNoPadding extends GenericAtomicSuite[Char, AtomicChar](
  Atomic.builderFor(0.toChar), NoPadding, x => x.toChar, x => x.toInt)

object GenericAtomicIntNoPadding extends GenericAtomicSuite[Int, AtomicInt](
  Atomic.builderFor(0), NoPadding, x => x, x => x)

object GenericAtomicLongNoPadding extends GenericAtomicSuite[Long, AtomicLong](
  Atomic.builderFor(0.toLong), NoPadding, x => x.toLong, x => x.toInt)

// -- Left64

object GenericAtomicAnyLeft64 extends GenericAtomicSuite[String, AtomicAny[String]](
  Atomic.builderFor(""), Left64, x => x.toString, x => x.toInt)

object GenericAtomicBooleanLeft64 extends GenericAtomicSuite[Boolean, AtomicBoolean](
  Atomic.builderFor(true), Left64, x => if (x == 1) true else false, x => if (x) 1 else 0)

object GenericAtomicNumberAnyLeft64 extends GenericAtomicSuite[BoxedLong, AtomicNumberAny[BoxedLong]](
  AtomicBuilder.AtomicNumberBuilder[BoxedLong], Left64, x => BoxedLong(x), x => x.value.toInt)

object GenericAtomicFloatLeft64 extends GenericAtomicSuite[Float, AtomicFloat](
  Atomic.builderFor(0.0f), Left64, x => x.toFloat, x => x.toInt)

object GenericAtomicDoubleLeft64 extends GenericAtomicSuite[Double, AtomicDouble](
  Atomic.builderFor(0.toDouble), Left64, x => x.toDouble, x => x.toInt)

object GenericAtomicShortLeft64 extends GenericAtomicSuite[Short, AtomicShort](
  Atomic.builderFor(0.toShort), Left64, x => x.toShort, x => x.toInt)

object GenericAtomicByteLeft64 extends GenericAtomicSuite[Byte, AtomicByte](
  Atomic.builderFor(0.toByte), Left64, x => x.toByte, x => x.toInt)

object GenericAtomicCharLeft64 extends GenericAtomicSuite[Char, AtomicChar](
  Atomic.builderFor(0.toChar), Left64, x => x.toChar, x => x.toInt)

object GenericAtomicIntLeft64 extends GenericAtomicSuite[Int, AtomicInt](
  Atomic.builderFor(0), Left64, x => x, x => x)

object GenericAtomicLongLeft64 extends GenericAtomicSuite[Long, AtomicLong](
  Atomic.builderFor(0.toLong), Left64, x => x.toLong, x => x.toInt)

// -- Right64

object GenericAtomicAnyRight64 extends GenericAtomicSuite[String, AtomicAny[String]](
  Atomic.builderFor(""), Right64, x => x.toString, x => x.toInt)

object GenericAtomicBooleanRight64 extends GenericAtomicSuite[Boolean, AtomicBoolean](
  Atomic.builderFor(true), Right64, x => if (x == 1) true else false, x => if (x) 1 else 0)

object GenericAtomicNumberAnyRight64 extends GenericAtomicSuite[BoxedLong, AtomicNumberAny[BoxedLong]](
  AtomicBuilder.AtomicNumberBuilder[BoxedLong], Right64, x => BoxedLong(x), x => x.value.toInt)

object GenericAtomicFloatRight64 extends GenericAtomicSuite[Float, AtomicFloat](
  Atomic.builderFor(0.0f), Right64, x => x.toFloat, x => x.toInt)

object GenericAtomicDoubleRight64 extends GenericAtomicSuite[Double, AtomicDouble](
  Atomic.builderFor(0.toDouble), Right64, x => x.toDouble, x => x.toInt)

object GenericAtomicShortRight64 extends GenericAtomicSuite[Short, AtomicShort](
  Atomic.builderFor(0.toShort), Right64, x => x.toShort, x => x.toInt)

object GenericAtomicByteRight64 extends GenericAtomicSuite[Byte, AtomicByte](
  Atomic.builderFor(0.toByte), Right64, x => x.toByte, x => x.toInt)

object GenericAtomicCharRight64 extends GenericAtomicSuite[Char, AtomicChar](
  Atomic.builderFor(0.toChar), Right64, x => x.toChar, x => x.toInt)

object GenericAtomicIntRight64 extends GenericAtomicSuite[Int, AtomicInt](
  Atomic.builderFor(0), Right64, x => x, x => x)

object GenericAtomicLongRight64 extends GenericAtomicSuite[Long, AtomicLong](
  Atomic.builderFor(0.toLong), Right64, x => x.toLong, x => x.toInt)

// -- LeftRight128

object GenericAtomicAnyLeftRight128 extends GenericAtomicSuite[String, AtomicAny[String]](
  Atomic.builderFor(""), LeftRight128, x => x.toString, x => x.toInt)

object GenericAtomicBooleanLeftRight128 extends GenericAtomicSuite[Boolean, AtomicBoolean](
  Atomic.builderFor(true), LeftRight128, x => if (x == 1) true else false, x => if (x) 1 else 0)

object GenericAtomicNumberAnyLeftRight128 extends GenericAtomicSuite[BoxedLong, AtomicNumberAny[BoxedLong]](
  AtomicBuilder.AtomicNumberBuilder[BoxedLong], LeftRight128, x => BoxedLong(x), x => x.value.toInt)

object GenericAtomicFloatLeftRight128 extends GenericAtomicSuite[Float, AtomicFloat](
  Atomic.builderFor(0.0f), LeftRight128, x => x.toFloat, x => x.toInt)

object GenericAtomicDoubleLeftRight128 extends GenericAtomicSuite[Double, AtomicDouble](
  Atomic.builderFor(0.toDouble), LeftRight128, x => x.toDouble, x => x.toInt)

object GenericAtomicShortLeftRight128 extends GenericAtomicSuite[Short, AtomicShort](
  Atomic.builderFor(0.toShort), LeftRight128, x => x.toShort, x => x.toInt)

object GenericAtomicByteLeftRight128 extends GenericAtomicSuite[Byte, AtomicByte](
  Atomic.builderFor(0.toByte), LeftRight128, x => x.toByte, x => x.toInt)

object GenericAtomicCharLeftRight128 extends GenericAtomicSuite[Char, AtomicChar](
  Atomic.builderFor(0.toChar), LeftRight128, x => x.toChar, x => x.toInt)

object GenericAtomicIntLeftRight128 extends GenericAtomicSuite[Int, AtomicInt](
  Atomic.builderFor(0), LeftRight128, x => x, x => x)

object GenericAtomicLongLeftRight128 extends GenericAtomicSuite[Long, AtomicLong](
  Atomic.builderFor(0.toLong), LeftRight128, x => x.toLong, x => x.toInt)
