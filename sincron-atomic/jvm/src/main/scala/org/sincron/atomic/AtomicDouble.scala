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
import org.sincron.atomic.boxes.{Factory, BoxedLong}
import scala.annotation.tailrec
import java.lang.Double.{longBitsToDouble, doubleToLongBits}

final class AtomicDouble private (val ref: BoxedLong)
  extends AtomicNumber[Double] {

  def get: Double = longBitsToDouble(ref.volatileGet())
  def set(update: Double): Unit = ref.volatileSet(doubleToLongBits(update))
  def update(value: Double): Unit = ref.volatileSet(doubleToLongBits(value))
  def `:=`(value: Double): Unit = ref.volatileSet(doubleToLongBits(value))
  def lazySet(update: Double): Unit = ref.lazySet(doubleToLongBits(update))

  def compareAndSet(expect: Double, update: Double): Boolean = {
    val expectLong = doubleToLongBits(expect)
    val updateLong = doubleToLongBits(update)
    ref.compareAndSet(expectLong, updateLong)
  }

  def getAndSet(update: Double): Double = {
    longBitsToDouble(ref.getAndSet(doubleToLongBits(update)))
  }

  @tailrec
  def increment(v: Int = 1): Unit = {
    val current = get
    val update = incrOp(current, v)
    if (!compareAndSet(current, update))
      increment(v)
  }

  @tailrec
  def add(v: Double): Unit = {
    val current = get
    val update = plusOp(current, v)
    if (!compareAndSet(current, update))
      add(v)
  }

  @tailrec
  def incrementAndGet(v: Int = 1): Double = {
    val current = get
    val update = incrOp(current, v)
    if (!compareAndSet(current, update))
      incrementAndGet(v)
    else
      update
  }

  @tailrec
  def addAndGet(v: Double): Double = {
    val current = get
    val update = plusOp(current, v)
    if (!compareAndSet(current, update))
      addAndGet(v)
    else
      update
  }

  @tailrec
  def getAndIncrement(v: Int = 1): Double = {
    val current = get
    val update = incrOp(current, v)
    if (!compareAndSet(current, update))
      getAndIncrement(v)
    else
      current
  }

  @tailrec
  def getAndAdd(v: Double): Double = {
    val current = get
    val update = plusOp(current, v)
    if (!compareAndSet(current, update))
      getAndAdd(v)
    else
      current
  }

  @tailrec
  def subtract(v: Double): Unit = {
    val current = get
    val update = minusOp(current, v)
    if (!compareAndSet(current, update))
      subtract(v)
  }

  @tailrec
  def subtractAndGet(v: Double): Double = {
    val current = get
    val update = minusOp(current, v)
    if (!compareAndSet(current, update))
      subtractAndGet(v)
    else
      update
  }

  @tailrec
  def getAndSubtract(v: Double): Double = {
    val current = get
    val update = minusOp(current, v)
    if (!compareAndSet(current, update))
      getAndSubtract(v)
    else
      current
  }

  @tailrec
  def countDownToZero(v: Double = 1.0): Double = {
    val current = get
    if (current != 0.0) {
      val decrement = if (current >= v) v else current
      val update = current - decrement
      if (!compareAndSet(current, update))
        countDownToZero(v)
      else
        decrement
    }
    else
      0.0
  }

  def decrement(v: Int = 1): Unit = increment(-v)
  def decrementAndGet(v: Int = 1): Double = incrementAndGet(-v)
  def getAndDecrement(v: Int = 1): Double = getAndIncrement(-v)
  def `+=`(v: Double): Unit = addAndGet(v)
  def `-=`(v: Double): Unit = subtractAndGet(v)

  private[this] def plusOp(a: Double, b: Double): Double = a + b
  private[this] def minusOp(a: Double, b: Double): Double = a - b
  private[this] def incrOp(a: Double, b: Int): Double = a + b
}

object AtomicDouble {
  def apply(initialValue: Double)(implicit strategy: PaddingStrategy = NoPadding): AtomicDouble =
    new AtomicDouble(Factory.newBoxedLong(
      doubleToLongBits(initialValue),
      boxStrategyToPaddingStrategy(strategy)))
}
