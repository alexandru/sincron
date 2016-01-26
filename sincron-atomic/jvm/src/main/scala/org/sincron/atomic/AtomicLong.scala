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

final class AtomicLong private (private[this] val ref: BoxedLong)
  extends AtomicNumber[Long] {

  def get: Long = ref.volatileGet()
  def set(update: Long): Unit = ref.volatileSet(update)
  def update(value: Long): Unit = ref.volatileSet(value)
  def `:=`(value: Long): Unit = ref.volatileSet(value)

  def compareAndSet(expect: Long, update: Long): Boolean = {
    ref.compareAndSet(expect, update)
  }

  def getAndSet(update: Long): Long = {
    ref.getAndSet(update)
  }

  def lazySet(update: Long): Unit = {
    ref.lazySet(update)
  }

  @tailrec
  def increment(v: Int = 1): Unit = {
    val current = ref.volatileGet()
    if (!ref.compareAndSet(current, current+v))
      increment(v)
  }

  @tailrec
  def incrementAndGet(v: Int = 1): Long = {
    val current = ref.volatileGet()
    val update = current + v
    if (!ref.compareAndSet(current, update))
      incrementAndGet(v)
    else
      update
  }

  @tailrec
  def getAndIncrement(v: Int = 1): Long = {
    val current = ref.volatileGet()
    val update = current + v
    if (!ref.compareAndSet(current, update))
      getAndIncrement(v)
    else
      current
  }

  @tailrec
  def getAndAdd(v: Long): Long = {
    val current = ref.volatileGet()
    val update = current + v
    if (!ref.compareAndSet(current, update))
      getAndAdd(v)
    else
      current
  }

  @tailrec
  def addAndGet(v: Long): Long = {
    val current = ref.volatileGet()
    val update = current + v
    if (!ref.compareAndSet(current, update))
      addAndGet(v)
    else
      update
  }

  @tailrec
  def add(v: Long): Unit = {
    val current = ref.volatileGet()
    val update = current + v
    if (!ref.compareAndSet(current, update))
      add(v)
  }

  def subtract(v: Long): Unit =
    add(-v)

  def getAndSubtract(v: Long): Long =
    getAndAdd(-v)

  def subtractAndGet(v: Long): Long =
    addAndGet(-v)

  @tailrec
  def countDownToZero(v: Long = 1): Long = {
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
  def decrementAndGet(v: Int = 1): Long = incrementAndGet(-v)
  def getAndDecrement(v: Int = 1): Long = getAndIncrement(-v)
  def `+=`(v: Long): Unit = addAndGet(v)
  def `-=`(v: Long): Unit = subtractAndGet(v)

  override def toString: String = s"AtomicLong(${ref.volatileGet()})"
}

object AtomicLong {
  def apply(initialValue: Long)(implicit strategy: PaddingStrategy = NoPadding): AtomicLong =
    new AtomicLong(Factory.newBoxedLong(initialValue, boxStrategyToPaddingStrategy(strategy)))
}
