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

final class AtomicByte private (private[this] val ref: BoxedInt)
  extends AtomicNumber[Byte] {

  private[this] val mask = 255

  def get: Byte =
    (ref.volatileGet() & mask).asInstanceOf[Byte]

  def set(update: Byte): Unit = ref.volatileSet(update)
  def update(value: Byte): Unit = ref.volatileSet(value)
  def `:=`(value: Byte): Unit = ref.volatileSet(value)

  def lazySet(update: Byte) = {
    ref.lazySet(update)
  }

  def compareAndSet(expect: Byte, update: Byte): Boolean = {
    ref.compareAndSet(expect, update)
  }

  def getAndSet(update: Byte): Byte = {
    (ref.getAndSet(update) & mask).asInstanceOf[Byte]
  }


  @tailrec
  def increment(v: Int = 1): Unit = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Byte]
    val update = incrOp(current, v)
    if (!ref.compareAndSet(current, update))
      increment(v)
  }

  @tailrec
  def add(v: Byte): Unit = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Byte]
    val update = plusOp(current, v)
    if (!ref.compareAndSet(current, update))
      add(v)
  }

  @tailrec
  def incrementAndGet(v: Int = 1): Byte = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Byte]
    val update = incrOp(current, v)
    if (!ref.compareAndSet(current, update))
      incrementAndGet(v)
    else
      update
  }

  @tailrec
  def addAndGet(v: Byte): Byte = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Byte]
    val update = plusOp(current, v)
    if (!ref.compareAndSet(current, update))
      addAndGet(v)
    else
      update
  }

  @tailrec
  def getAndIncrement(v: Int = 1): Byte = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Byte]
    val update = incrOp(current, v)
    if (!ref.compareAndSet(current, update))
      getAndIncrement(v)
    else
      current
  }

  @tailrec
  def getAndAdd(v: Byte): Byte = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Byte]
    val update = plusOp(current, v)
    if (!ref.compareAndSet(current, update))
      getAndAdd(v)
    else
      current
  }

  @tailrec
  def subtract(v: Byte): Unit = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Byte]
    val update = minusOp(current, v)
    if (!ref.compareAndSet(current, update))
      subtract(v)
  }

  @tailrec
  def subtractAndGet(v: Byte): Byte = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Byte]
    val update = minusOp(current, v)
    if (!ref.compareAndSet(current, update))
      subtractAndGet(v)
    else
      update
  }

  @tailrec
  def getAndSubtract(v: Byte): Byte = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Byte]
    val update = minusOp(current, v)
    if (!ref.compareAndSet(current, update))
      getAndSubtract(v)
    else
      current
  }

  @tailrec
  def countDownToZero(v: Byte = 1): Byte = {
    val current = (ref.volatileGet() & mask).asInstanceOf[Byte]
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
  def decrementAndGet(v: Int = 1): Byte = incrementAndGet(-v)
  def getAndDecrement(v: Int = 1): Byte = getAndIncrement(-v)
  def `+=`(v: Byte): Unit = addAndGet(v)
  def `-=`(v: Byte): Unit = subtractAndGet(v)

  private[this] def plusOp(a: Byte, b: Byte): Byte =
    ((a + b) & mask).asInstanceOf[Byte]

  private[this] def minusOp(a: Byte, b: Byte): Byte =
    ((a - b) & mask).asInstanceOf[Byte]

  private[this] def incrOp(a: Byte, b: Int): Byte =
    ((a + b) & mask).asInstanceOf[Byte]
}

object AtomicByte {
  def apply(initialValue: Byte)(implicit strategy: PaddingStrategy = NoPadding): AtomicByte =
    new AtomicByte(Factory.newBoxedInt(initialValue, boxStrategyToPaddingStrategy(strategy)))
}
