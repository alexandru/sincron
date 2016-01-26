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

final class AtomicAny[T <: AnyRef] private (private[this] val ref: BoxedObject) extends Atomic[T] {
  def get: T = ref.volatileGet().asInstanceOf[T]

  def set(update: T): Unit = ref.volatileSet(update)
  def update(value: T): Unit = ref.volatileSet(value)
  def `:=`(value: T): Unit = ref.volatileSet(value)

  def compareAndSet(expect: T, update: T): Boolean = {
    ref.compareAndSet(expect, update)
  }

  def getAndSet(update: T): T = {
    ref.getAndSet(update).asInstanceOf[T]
  }

  def lazySet(update: T): Unit = {
    ref.lazySet(update)
  }
}

object AtomicAny {
  def apply[T <: AnyRef](initialValue: T)(implicit strategy: PaddingStrategy = NoPadding): AtomicAny[T] =
    new AtomicAny[T](Factory.newBoxedObject(initialValue, boxStrategyToPaddingStrategy(strategy)))
}
