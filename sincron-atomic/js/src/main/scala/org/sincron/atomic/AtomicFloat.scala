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

final class AtomicFloat private[atomic]
  (initialValue: Float) extends AtomicNumber[Float] {

  private[this] var ref = initialValue

  def getAndSet(update: Float): Float = {
    val current = ref
    ref = update
    current
  }

  def compareAndSet(expect: Float, update: Float): Boolean = {
    if (ref == expect) {
      ref = update
      true
    }
    else
      false
  }

  def set(update: Float): Unit = {
    ref = update
  }

  def get: Float = ref

  def getAndSubtract(v: Float): Float = {
    val c = ref
    ref = ref - v
    c
  }

  def subtractAndGet(v: Float): Float = {
    ref = ref - v
    ref
  }

  def subtract(v: Float): Unit = {
    ref = ref - v
  }

  def getAndAdd(v: Float): Float = {
    val c = ref
    ref = ref + v
    c
  }

  def getAndIncrement(v: Int = 1): Float = {
    val c = ref
    ref = ref + v
    c
  }

  def addAndGet(v: Float): Float = {
    ref = ref + v
    ref
  }

  def incrementAndGet(v: Int = 1): Float = {
    ref = ref + v
    ref
  }

  def add(v: Float): Unit = {
    ref = ref + v
  }

  def increment(v: Int = 1): Unit = {
    ref = ref + v
  }

  def decrement(v: Int = 1): Unit = increment(-v)
  def decrementAndGet(v: Int = 1): Float = incrementAndGet(-v)
  def getAndDecrement(v: Int = 1): Float = getAndIncrement(-v)
}

object AtomicFloat {
  def apply(initialValue: Float): AtomicFloat =
    new AtomicFloat(initialValue)
}