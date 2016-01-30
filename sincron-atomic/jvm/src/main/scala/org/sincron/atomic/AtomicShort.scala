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

final class AtomicShort private (private[this] val ref: BoxedInt)
  extends AtomicNumber[Short] {
  private[this] val mask = 255 + 255 * 256

  def get: Short = (ref.volatileGet() & mask).asInstanceOf[Short]
  def set(update: Short): Unit = ref.volatileSet(update)

  def lazySet(update: Short) = {
    ref.lazySet(update)
  }

  def compareAndSet(expect: Short, update: Short): Boolean = {
    ref.compareAndSet(expect, update)
  }

  def getAndSet(update: Short): Short = {
    (ref.getAndSet(update) & mask).asInstanceOf[Short]
  }


  @tailrec
  def increment(v: Int = 1): Unit = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Short]
    val update = incrementOp(current, v)
    if (!ref.compareAndSet(current, update))
      increment(v)
  }

  @tailrec
  def add(v: Short): Unit = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Short]
    val update = plusOp(current, v)
    if (!ref.compareAndSet(current, update))
      add(v)
  }

  @tailrec
  def incrementAndGet(v: Int = 1): Short = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Short]
    val update = incrementOp(current, v)
    if (!ref.compareAndSet(current, update))
      incrementAndGet(v)
    else
      update
  }

  @tailrec
  def addAndGet(v: Short): Short = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Short]
    val update = plusOp(current, v)
    if (!ref.compareAndSet(current, update))
      addAndGet(v)
    else
      update
  }

  @tailrec
  def getAndIncrement(v: Int = 1): Short = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Short]
    val update = incrementOp(current, v)
    if (!ref.compareAndSet(current, update))
      getAndIncrement(v)
    else
      current
  }

  @tailrec
  def getAndAdd(v: Short): Short = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Short]
    val update = plusOp(current, v)
    if (!ref.compareAndSet(current, update))
      getAndAdd(v)
    else
      current
  }

  @tailrec
  def subtract(v: Short): Unit = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Short]
    val update = minusOp(current, v)
    if (!ref.compareAndSet(current, update))
      subtract(v)
  }

  @tailrec
  def subtractAndGet(v: Short): Short = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Short]
    val update = minusOp(current, v)
    if (!ref.compareAndSet(current, update))
      subtractAndGet(v)
    else
      update
  }

  @tailrec
  def getAndSubtract(v: Short): Short = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Short]
    val update = minusOp(current, v)
    if (!ref.compareAndSet(current, update))
      getAndSubtract(v)
    else
      current
  }

  def decrement(v: Int = 1): Unit = increment(-v)
  def decrementAndGet(v: Int = 1): Short = incrementAndGet(-v)
  def getAndDecrement(v: Int = 1): Short = getAndIncrement(-v)

  private[this] def plusOp(a: Short, b: Short): Short =
    ((a + b) & mask).asInstanceOf[Short]

  private[this] def minusOp(a: Short, b: Short): Short =
    ((a - b) & mask).asInstanceOf[Short]

  private[this] def incrementOp(a: Short, b: Int): Short =
    ((a + b) & mask).asInstanceOf[Short]
}

object AtomicShort {
  def apply(initialValue: Short)(implicit strategy: PaddingStrategy = NoPadding): AtomicShort =
    new AtomicShort(Factory.newBoxedInt(initialValue, boxStrategyToPaddingStrategy(strategy)))
}
