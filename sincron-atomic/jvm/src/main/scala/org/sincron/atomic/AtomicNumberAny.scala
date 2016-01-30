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
import org.sincron.atomic.boxes.{Factory, BoxedObject}
import scala.annotation.tailrec

final class AtomicNumberAny[T <: AnyRef : Numeric] private (private[this] val ref: BoxedObject)
  extends AtomicNumber[T] {
  
  private[this] val ev = implicitly[Numeric[T]]

  def get: T = ref.volatileGet().asInstanceOf[T]
  def set(update: T): Unit = ref.volatileSet(update)

  def compareAndSet(expect: T, update: T): Boolean = {
    ref.compareAndSet(expect, update)
  }

  def getAndSet(update: T): T = {
    ref.getAndSet(update).asInstanceOf[T]
  }

  def lazySet(update: T): Unit = {
    ref.lazySet(update)
  }

  @tailrec
  def increment(v: Int = 1): Unit = {
    val current = get
    if (!compareAndSet(current, ev.plus(current, ev.fromInt(v))))
      increment(v)
  }

  @tailrec
  def incrementAndGet(v: Int = 1): T = {
    val current = get
    val update = ev.plus(current, ev.fromInt(v))
    if (!compareAndSet(current, update))
      incrementAndGet(v)
    else
      update
  }

  @tailrec
  def getAndIncrement(v: Int = 1): T = {
    val current = get
    val update = ev.plus(current, ev.fromInt(v))
    if (!compareAndSet(current, update))
      getAndIncrement(v)
    else
      current
  }

  @tailrec
  def getAndAdd(v: T): T = {
    val current = get
    val update = ev.plus(current, v)
    if (!compareAndSet(current, update))
      getAndAdd(v)
    else
      current
  }

  @tailrec
  def addAndGet(v: T): T = {
    val current = get
    val update = ev.plus(current, v)
    if (!compareAndSet(current, update))
      addAndGet(v)
    else
      update
  }

  @tailrec
  def add(v: T): Unit = {
    val current = get
    val update = ev.plus(current, v)
    if (!compareAndSet(current, update))
      add(v)
  }

  @tailrec
  def subtract(v: T): Unit = {
    val current = get
    val update = ev.minus(current, v)
    if (!compareAndSet(current, update))
      subtract(v)
  }

  @tailrec
  def getAndSubtract(v: T): T = {
    val current = get
    val update = ev.minus(current, v)
    if (!compareAndSet(current, update))
      getAndSubtract(v)
    else
      current
  }

  @tailrec
  def subtractAndGet(v: T): T = {
    val current = get
    val update = ev.minus(current, v)
    if (!compareAndSet(current, update))
      subtractAndGet(v)
    else
      update
  }

  def decrement(v: Int = 1): Unit = increment(-v)
  def decrementAndGet(v: Int = 1): T = incrementAndGet(-v)
  def getAndDecrement(v: Int = 1): T = getAndIncrement(-v)
}

object AtomicNumberAny {
  def apply[T <: AnyRef : Numeric](initialValue: T)
    (implicit strategy: PaddingStrategy = NoPadding): AtomicNumberAny[T] =
    new AtomicNumberAny(Factory.newBoxedObject(initialValue, boxStrategyToPaddingStrategy(strategy)))
}
