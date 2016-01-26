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
import org.sincron.atomic.boxes.{Factory, BoxedInt}
import scala.annotation.tailrec

final class AtomicInt private (private[this] val ref: BoxedInt)
  extends AtomicNumber[Int] {

  def get: Int = ref.volatileGet()
  def set(update: Int): Unit = ref.volatileSet(update)
  def update(value: Int): Unit = ref.volatileSet(value)
  def `:=`(value: Int): Unit = ref.volatileSet(value)

  def compareAndSet(expect: Int, update: Int): Boolean = {
    ref.compareAndSet(expect, update)
  }

  def getAndSet(update: Int): Int = {
    ref.getAndSet(update)
  }

  def lazySet(update: Int): Unit = {
    ref.lazySet(update)
  }

  @tailrec
  def increment(v: Int = 1): Unit = {
    val current = ref.volatileGet()
    if (!ref.compareAndSet(current, current+v))
      increment(v)
  }

  @tailrec
  def incrementAndGet(v: Int = 1): Int = {
    val current = ref.volatileGet()
    val update = current + v
    if (!ref.compareAndSet(current, update))
      incrementAndGet(v)
    else
      update
  }

  @tailrec
  def getAndIncrement(v: Int = 1): Int = {
    val current = ref.volatileGet()
    val update = current + v
    if (!ref.compareAndSet(current, update))
      getAndIncrement(v)
    else
      current
  }

  @tailrec
  def getAndAdd(v: Int): Int = {
    val current = ref.volatileGet()
    val update = current + v
    if (!ref.compareAndSet(current, update))
      getAndAdd(v)
    else
      current
  }

  @tailrec
  def addAndGet(v: Int): Int = {
    val current = ref.volatileGet()
    val update = current + v
    if (!ref.compareAndSet(current, update))
      addAndGet(v)
    else
      update
  }

  @tailrec
  def add(v: Int): Unit = {
    val current = ref.volatileGet()
    val update = current + v
    if (!ref.compareAndSet(current, update))
      add(v)
  }

  def subtract(v: Int): Unit =
    add(-v)

  def getAndSubtract(v: Int): Int =
    getAndAdd(-v)

  def subtractAndGet(v: Int): Int =
    addAndGet(-v)

  @tailrec
  def countDownToZero(v: Int = 1): Int = {
    val current = get
    if (current != 0) {
      val decrement = if (current >= v) v else current
      val update = current - decrement
      if (!ref.compareAndSet(current, update))
        countDownToZero(v)
      else
        decrement
    }
    else
      0
  }

  def decrement(v: Int = 1): Unit = increment(-v)
  def decrementAndGet(v: Int = 1): Int = incrementAndGet(-v)
  def getAndDecrement(v: Int = 1): Int = getAndIncrement(-v)
  def `+=`(v: Int): Unit = addAndGet(v)
  def `-=`(v: Int): Unit = subtractAndGet(v)

  override def toString: String = s"AtomicInt(${ref.volatileGet()})"
}

object AtomicInt {
  def apply(initialValue: Int)(implicit strategy: PaddingStrategy = NoPadding): AtomicInt =
    new AtomicInt(Factory.newBoxedInt(initialValue, boxStrategyToPaddingStrategy(strategy)))
}
