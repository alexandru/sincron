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

import java.util.concurrent.atomic.{AtomicInteger => JavaAtomicInteger}
import scala.annotation.tailrec

class AtomicChar private (ref: JavaAtomicInteger)
  extends AtomicNumber[Char] {

  private[this] val mask = 255 + 255 * 256

  final def get: Char =
    (ref.get & mask).asInstanceOf[Char]

  final def set(update: Char) = {
    ref.set(update)
  }

  final def lazySet(update: Char) = {
    ref.lazySet(update)
  }

  final def compareAndSet(expect: Char, update: Char): Boolean = {
    ref.compareAndSet(expect, update)
  }

  final def getAndSet(update: Char): Char = {
    (ref.getAndSet(update) & mask).asInstanceOf[Char]
  }

  final def update(value: Char): Unit = set(value)
  final def `:=`(value: Char): Unit = set(value)

  @tailrec
  final def increment(v: Int = 1): Unit = {
    val current = get
    val update = incrOp(current, v)
    if (!compareAndSet(current, update))
      increment(v)
  }

  @tailrec
  final def add(v: Char): Unit = {
    val current = get
    val update = plusOp(current, v)
    if (!compareAndSet(current, update))
      add(v)
  }

  @tailrec
  final def incrementAndGet(v: Int = 1): Char = {
    val current = get
    val update = incrOp(current, v)
    if (!compareAndSet(current, update))
      incrementAndGet(v)
    else
      update
  }

  @tailrec
  final def addAndGet(v: Char): Char = {
    val current = get
    val update = plusOp(current, v)
    if (!compareAndSet(current, update))
      addAndGet(v)
    else
      update
  }

  @tailrec
  final def getAndIncrement(v: Int = 1): Char = {
    val current = get
    val update = incrOp(current, v)
    if (!compareAndSet(current, update))
      getAndIncrement(v)
    else
      current
  }

  @tailrec
  final def getAndAdd(v: Char): Char = {
    val current = get
    val update = plusOp(current, v)
    if (!compareAndSet(current, update))
      getAndAdd(v)
    else
      current
  }

  @tailrec
  final def subtract(v: Char): Unit = {
    val current = get
    val update = minusOp(current, v)
    if (!compareAndSet(current, update))
      subtract(v)
  }

  @tailrec
  final def subtractAndGet(v: Char): Char = {
    val current = get
    val update = minusOp(current, v)
    if (!compareAndSet(current, update))
      subtractAndGet(v)
    else
      update
  }

  @tailrec
  final def getAndSubtract(v: Char): Char = {
    val current = get
    val update = minusOp(current, v)
    if (!compareAndSet(current, update))
      getAndSubtract(v)
    else
      current
  }

  @tailrec
  final def countDownToZero(v: Char = 1): Char = {
    val current = get
    if (current != 0) {
      val decrement = if (current >= v) v else current
      val update = minusOp(current, decrement)
      if (!compareAndSet(current, update))
        countDownToZero(v)
      else
        decrement
    }
    else
      0
  }

  final def decrement(v: Int = 1): Unit = increment(-v)
  final def decrementAndGet(v: Int = 1): Char = incrementAndGet(-v)
  final def getAndDecrement(v: Int = 1): Char = getAndIncrement(-v)
  final def `+=`(v: Char): Unit = addAndGet(v)
  final def `-=`(v: Char): Unit = subtractAndGet(v)

  private[this] final def plusOp(a: Char, b: Char): Char =
    ((a + b) & mask).asInstanceOf[Char]

  private[this] final def minusOp(a: Char, b: Char): Char =
    ((a - b) & mask).asInstanceOf[Char]

  private[this] final def incrOp(a: Char, b: Int): Char =
    ((a + b) & mask).asInstanceOf[Char]
}

object AtomicChar {
  def apply(initialValue: Char): AtomicChar =
    new AtomicChar(new JavaAtomicInteger(initialValue))

  def wrap(ref: JavaAtomicInteger): AtomicChar =
    new AtomicChar(ref)
}
