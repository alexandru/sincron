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

import java.util.concurrent.atomic.AtomicReference

final class AtomicAny[T] private (ref: AtomicReference[T]) extends Atomic[T] {
  def get: T = ref.get()

  def set(update: T): Unit = {
    ref.set(update)
  }

  def update(value: T): Unit = set(value)
  def `:=`(value: T): Unit = set(value)

  def compareAndSet(expect: T, update: T): Boolean = {
    val current = ref.get()
    current == expect && ref.compareAndSet(current, update)
  }

  def getAndSet(update: T): T = {
    ref.getAndSet(update)
  }

  def lazySet(update: T): Unit = {
    ref.lazySet(update)
  }
}

object AtomicAny {
  def apply[T](initialValue: T): AtomicAny[T] =
    new AtomicAny[T](new AtomicReference[T](initialValue))

  def wrap[T](ref: AtomicReference[T]): AtomicAny[T] =
    new AtomicAny[T](ref)
}
