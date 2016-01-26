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


final class AtomicShort private[atomic]
(initialValue: Short) extends AtomicNumber[Short] {

  private[this] var ref = initialValue
  private[this] val mask = 255 + 255 * 256

  def getAndSet(update: Short): Short = {
    val current = ref
    ref = update
    current
  }

  def compareAndSet(expect: Short, update: Short): Boolean = {
    if (ref == expect) {
      ref = update
      true
    }
    else
      false
  }

  def set(update: Short): Unit = {
    ref = update
  }

  def get: Short = ref

  @inline
  def update(value: Short): Unit = set(value)

  @inline
  def `:=`(value: Short): Unit = set(value)

  @inline
  def lazySet(update: Short): Unit = set(update)

  def getAndSubtract(v: Short): Short = {
    val c = ref
    ref = ((ref - v) & mask).asInstanceOf[Short]
    c
  }

  def subtractAndGet(v: Short): Short = {
    ref = ((ref - v) & mask).asInstanceOf[Short]
    ref
  }

  def subtract(v: Short): Unit = {
    ref = ((ref - v) & mask).asInstanceOf[Short]
  }

  def getAndAdd(v: Short): Short = {
    val c = ref
    ref = ((ref + v) & mask).asInstanceOf[Short]
    c
  }

  def getAndIncrement(v: Int = 1): Short = {
    val c = ref
    ref = ((ref + v) & mask).asInstanceOf[Short]
    c
  }

  def addAndGet(v: Short): Short = {
    ref = ((ref + v) & mask).asInstanceOf[Short]
    ref
  }

  def incrementAndGet(v: Int = 1): Short = {
    ref = ((ref + v) & mask).asInstanceOf[Short]
    ref
  }

  def add(v: Short): Unit = {
    ref = ((ref + v) & mask).asInstanceOf[Short]
  }

  def increment(v: Int = 1): Unit = {
    ref = ((ref + v) & mask).asInstanceOf[Short]
  }

  def countDownToZero(v: Short = 1): Short = {
    val current = get
    if (current != 0) {
      val decrement = if (current >= v) v else current
      ref = ((current - decrement) & mask).asInstanceOf[Short]
      decrement
    }
    else
      0
  }

  def decrement(v: Int = 1): Unit = increment(-v)
  def decrementAndGet(v: Int = 1): Short = incrementAndGet(-v)
  def getAndDecrement(v: Int = 1): Short = getAndIncrement(-v)
  def `+=`(v: Short): Unit = addAndGet(v)
  def `-=`(v: Short): Unit = subtractAndGet(v)
}

object AtomicShort {
  def apply(initialValue: Short): AtomicShort =
    new AtomicShort(initialValue)
}