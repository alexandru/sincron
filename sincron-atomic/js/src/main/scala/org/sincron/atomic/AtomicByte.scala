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


final class AtomicByte private[atomic]
  (initialValue: Byte) extends AtomicNumber[Byte] {

  private[this] var ref = initialValue
  private[this] val mask = 255

  def getAndSet(update: Byte): Byte = {
    val current = ref
    ref = update
    current
  }

  def compareAndSet(expect: Byte, update: Byte): Boolean = {
    if (ref == expect) {
      ref = update
      true
    }
    else
      false
  }

  def set(update: Byte): Unit = {
    ref = update
  }

  def get: Byte = ref

  @inline
  def update(value: Byte): Unit = set(value)

  @inline
  def `:=`(value: Byte): Unit = set(value)

  @inline
  def lazySet(update: Byte): Unit = set(update)

  def getAndSubtract(v: Byte): Byte = {
    val c = ref
    ref = ((ref - v) & mask).asInstanceOf[Byte]
    c
  }

  def subtractAndGet(v: Byte): Byte = {
    ref = ((ref - v) & mask).asInstanceOf[Byte]
    ref
  }

  def subtract(v: Byte): Unit = {
    ref = ((ref - v) & mask).asInstanceOf[Byte]
  }

  def getAndAdd(v: Byte): Byte = {
    val c = ref
    ref = ((ref + v) & mask).asInstanceOf[Byte]
    c
  }

  def getAndIncrement(v: Int = 1): Byte = {
    val c = ref
    ref = ((ref + v) & mask).asInstanceOf[Byte]
    c
  }

  def addAndGet(v: Byte): Byte = {
    ref = ((ref + v) & mask).asInstanceOf[Byte]
    ref
  }

  def incrementAndGet(v: Int = 1): Byte = {
    ref = ((ref + v) & mask).asInstanceOf[Byte]
    ref
  }

  def add(v: Byte): Unit = {
    ref = ((ref + v) & mask).asInstanceOf[Byte]
  }

  def increment(v: Int = 1): Unit = {
    ref = ((ref + v) & mask).asInstanceOf[Byte]
  }

  def countDownToZero(v: Byte = 1): Byte = {
    val current = get
    if (current != 0) {
      val decrement = if (current >= v) v else current
      ref = ((current - decrement) & mask).asInstanceOf[Byte]
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
}

object AtomicByte {
  def apply(initialValue: Byte): AtomicByte =
    new AtomicByte(initialValue)
}