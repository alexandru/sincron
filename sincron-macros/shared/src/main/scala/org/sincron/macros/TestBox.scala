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

package org.sincron.macros

import org.sincron.macros.compat._
import scala.language.experimental.macros

/** Represents a boxed value, to be used in the testing
  * of [[InlineUtil]].
  */
private[macros] final case class TestBox[T](value: T) {
  def map[U](f: T => U): TestBox[U] = macro TestBox.Macros.mapMacroImpl[T,U]
}

/** Represents a boxed value, to be used in the testing
  * of [[InlineUtil]].
  */
private[macros] object TestBox {
  object Macros {
    def mapMacroImpl[T : c.WeakTypeTag, U : c.WeakTypeTag]
      (c: Context { type PrefixType = TestBox[T] })
      (f: c.Expr[T => U]): c.Expr[TestBox[U]] = {

      import c.universe._
      val util = SyntaxUtil[c.type](c)
      val selfExpr: c.Expr[TestBox[T]] = c.prefix

      val tree =
        if (util.isClean(selfExpr, f)) {
          q"""
          TestBox($f($selfExpr.value))
          """
        }
        else {
          val self = util.name("self")
          val fn = util.name("fn")

          q"""
          val $self = $selfExpr
          val $fn = $f
          TestBox($fn($self.value))
          """
        }

      new InlineUtil[c.type](c).inlineAndReset[TestBox[U]](tree)
    }
  }
}
