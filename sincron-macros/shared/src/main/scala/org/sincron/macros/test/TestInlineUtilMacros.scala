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

package org.sincron.macros.test

import org.sincron.macros.InlineUtil

import scala.language.experimental.macros

object TestInlineUtilMacros {
  import org.sincron.macros.compat._



  def testInlineMacro(): Boolean = macro testInlineMacroImpl

  def testInlineMacroImpl(c: Context)() = {
    import c.universe._
    val inliner = new InlineUtil[c.type](c)

    val result:Boolean = List({
        val actual = inliner.inlineAndReset(q"((x:Int) => x + 1)(10)").tree
        val expected = q"10 + 1"
        val r = actual equalsStructure expected
        if(!r)
          println(s"Expected ${expected} but got ${actual}")
        r
      },
      {
        val actual = inliner.inlineAndReset(q"((x:Int) => x + 1).apply(10)").tree
        val expected = q"10 + 1"
        val r = actual equalsStructure expected
        if(!r)
          println(s"Expected ${expected} but got ${actual}")
        r
    }).forall(x => x)

    q"$result"
  }

  def testInlineMultipleArgsMacro(): Boolean = macro testInlineMultipleArgsMacroImpl

  def testInlineMultipleArgsMacroImpl(c: Context)() = {
    import c.universe._
    val inliner = new InlineUtil[c.type](c)

    val inlinedNoApplyFct = inliner.inlineAndReset(q"((x:Int, y:Int) => {val z = x + 1; y + z})(10, 20)").tree
    val resultNoApply = inlinedNoApplyFct equalsStructure q"{val z = 10 + 1; 20 + z}"

    val result:Boolean = List({
        val actual = inliner.inlineAndReset(q"((x:Int, y:Int) => {val z = x + 1; y + z})(10, 20)").tree
        val expected = q"{val z = 10 + 1; 20 + z}"
        val r = actual equalsStructure expected
        if(!r)
          println(s"Expected ${expected} but got ${actual}")
        r
      },
      {
        val actual = inliner.inlineAndReset(q"((x:Int) => x + 1).apply(10)").tree
        val expected = q"10 + 1"
        val r = actual equalsStructure expected
        if(!r)
          println(s"Expected ${expected} but got ${actual}")
        r
      }
    ).forall(x => x)

    q"$result"
  }
}
