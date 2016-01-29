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

package org.sincron

import org.sincron.atomic.PaddingStrategy._
import org.sincron.atomic.boxes.BoxPaddingStrategy

/** A small toolkit of classes that support compare-and-swap semantics for safe mutation of variables.
  *
  * On top of the JVM, this means dealing with lock-free thread-safe programming. Also works on top of Javascript,
  * with Scala.js (for good reasons, as Atomic references are still useful in non-multi-threaded environments).
  *
  * The backbone of Atomic references is this method:
  * {{{
  *   def compareAndSet(expect: T, update: T): Boolean
  * }}}
  *
  * This method atomically sets a variable to the `update` value if it currently holds
  * the `expect` value, reporting `true` on success or `false` on failure. The classes in this package
  * also contain methods to get and unconditionally set values. They also support weak operations,
  * defined in `WeakAtomic[T]`, such as (e.g. `weakCompareAndSet`, `lazySet`) or operations that
  * block the current thread through ''spin-locking'', until a condition happens (e.g. `waitForCompareAndSet`),
  * methods exposed by `BlockingAtomic[T]`.
  *
  * Building a reference is easy with the provided constructor, which will automatically return the
  * most specific type needed (in the following sample, that's an `AtomicDouble`, inheriting from `AtomicNumber[T]`):
  * {{{
  *   val atomicNumber = Atomic(12.2)
  *
  *   atomicNumber.incrementAndGet()
  *   // => 13.2
  * }}}
  *
  * In comparison with `java.util.concurrent.AtomicReference`, these references implement common interfaces
  * that you can use generically (i.e. `Atomic[T]`, `AtomicNumber[T]`).
  * And also provide useful helpers for atomically mutating of values
  * (i.e. `transform`, `transformAndGet`, `getAndTransform`, etc...) or of numbers of any kind
  * (`incrementAndGet`, `getAndAdd`, etc...).
  */
package object atomic {
  private[sincron] def boxStrategyToPaddingStrategy(s: PaddingStrategy): BoxPaddingStrategy =
    s match {
      case NoPadding =>
        BoxPaddingStrategy.NO_PADDING
      case Left64 =>
        BoxPaddingStrategy.LEFT_64
      case Right64 =>
        BoxPaddingStrategy.RIGHT_64
      case LeftRight128 =>
        BoxPaddingStrategy.LEFT_RIGHT_128
      case Left128 =>
        BoxPaddingStrategy.LEFT_128
      case Right128 =>
        BoxPaddingStrategy.RIGHT_128
      case LeftRight256 =>
        BoxPaddingStrategy.LEFT_RIGHT_256
    }
}