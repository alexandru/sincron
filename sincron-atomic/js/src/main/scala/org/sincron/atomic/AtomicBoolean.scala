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

final class AtomicBoolean private[atomic]
  (initialValue: Boolean) extends Atomic[Boolean] {

  private[this] var ref = initialValue

  def getAndSet(update: Boolean): Boolean = {
    val current = ref
    ref = update
    current
  }

  def compareAndSet(expect: Boolean, update: Boolean): Boolean = {
    if (ref == expect) {
      ref = update
      true
    }
    else
      false
  }

  def set(update: Boolean): Unit = ref = update
  def update(value: Boolean): Unit = ref = value
  def `:=`(value: Boolean): Unit = ref = value
  def lazySet(update: Boolean): Unit = ref = update
  def get: Boolean = ref
}

object AtomicBoolean {
  def apply(initialValue: Boolean): AtomicBoolean =
    new AtomicBoolean(initialValue)
}
