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
import org.sincron.atomic.boxes.{BoxedInt, Factory}
import scala.annotation.tailrec

final class AtomicChar private (private[this] val ref: BoxedInt)
  extends AtomicNumber[Char] {
  private[this] val mask = 255 + 255 * 256

  def get: Char = (ref.volatileGet() & mask).asInstanceOf[Char]
  def set(update: Char): Unit = ref.volatileSet(update)
  def update(value: Char): Unit = ref.volatileSet(value)
  def `:=`(value: Char): Unit = ref.volatileSet(value)

  def lazySet(update: Char) = {
    ref.lazySet(update)
  }

  def compareAndSet(expect: Char, update: Char): Boolean = {
    ref.compareAndSet(expect, update)
  }

  def getAndSet(update: Char): Char = {
    (ref.getAndSet(update) & mask).asInstanceOf[Char]
  }


  @tailrec
  def increment(v: Int = 1): Unit = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Char]
    val update = incrOp(current, v)
    if (!ref.compareAndSet(current, update))
      increment(v)
  }

  @tailrec
  def add(v: Char): Unit = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Char]
    val update = plusOp(current, v)
    if (!ref.compareAndSet(current, update))
      add(v)
  }

  @tailrec
  def incrementAndGet(v: Int = 1): Char = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Char]
    val update = incrOp(current, v)
    if (!ref.compareAndSet(current, update))
      incrementAndGet(v)
    else
      update
  }

  @tailrec
  def addAndGet(v: Char): Char = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Char]
    val update = plusOp(current, v)
    if (!ref.compareAndSet(current, update))
      addAndGet(v)
    else
      update
  }

  @tailrec
  def getAndIncrement(v: Int = 1): Char = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Char]
    val update = incrOp(current, v)
    if (!ref.compareAndSet(current, update))
      getAndIncrement(v)
    else
      current
  }

  @tailrec
  def getAndAdd(v: Char): Char = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Char]
    val update = plusOp(current, v)
    if (!ref.compareAndSet(current, update))
      getAndAdd(v)
    else
      current
  }

  @tailrec
  def subtract(v: Char): Unit = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Char]
    val update = minusOp(current, v)
    if (!ref.compareAndSet(current, update))
      subtract(v)
  }

  @tailrec
  def subtractAndGet(v: Char): Char = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Char]
    val update = minusOp(current, v)
    if (!ref.compareAndSet(current, update))
      subtractAndGet(v)
    else
      update
  }

  @tailrec
  def getAndSubtract(v: Char): Char = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Char]
    val update = minusOp(current, v)
    if (!ref.compareAndSet(current, update))
      getAndSubtract(v)
    else
      current
  }

  @tailrec
  def countDownToZero(v: Char = 1): Char = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Char]
    if (current != 0) {
      val decrement = if (current >= v) v else current
      val update = minusOp(current, decrement)
      if (!ref.compareAndSet(current, update))
        countDownToZero(v)
      else
        decrement
    }
    else
      0
  }

  def decrement(v: Int = 1): Unit = increment(-v)
  def decrementAndGet(v: Int = 1): Char = incrementAndGet(-v)
  def getAndDecrement(v: Int = 1): Char = getAndIncrement(-v)
  def `+=`(v: Char): Unit = addAndGet(v)
  def `-=`(v: Char): Unit = subtractAndGet(v)

  private[this] def plusOp(a: Char, b: Char): Char =
    ((a + b) & mask).asInstanceOf[Char]

  private[this] def minusOp(a: Char, b: Char): Char =
    ((a - b) & mask).asInstanceOf[Char]

  private[this] def incrOp(a: Char, b: Int): Char =
    ((a + b) & mask).asInstanceOf[Char]
}

object AtomicChar {
  def apply(initialValue: Char)(implicit strategy: PaddingStrategy = NoPadding): AtomicChar =
    new AtomicChar(Factory.newBoxedInt(initialValue, boxStrategyToPaddingStrategy(strategy)))
}

