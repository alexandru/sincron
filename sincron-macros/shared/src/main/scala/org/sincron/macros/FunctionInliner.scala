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

import compat._

class FunctionInliner[C <: Context](val c: C) {
  import c.universe._

  val ApplyMethod = TermName("apply")

  def inlineParam(paramTermName: TermName, arg: Tree, body: Tree): Tree = new Transformer {
    override def transform(tree: Tree): Tree = tree match {
      case i@Ident(_) if i.name == paramTermName => arg

      case _ => super.transform(tree)
    }
  }.transform(body)

  def inline(function: Tree): Tree = new Transformer {
    override def transform(tree: Tree): Tree = tree match {

      case Apply(Function(params,body),args) =>
        params.zip(args).foldLeft(body){ (b,paramArgs) =>
          val (param, arg) = paramArgs
          inlineParam(param.name, arg, b)
        }

      case Apply(Select(Function(params,body), ApplyMethod), args) =>
        params.zip(args).foldLeft(body){ (b,paramArgs) =>
          val (param, arg) = paramArgs
          inlineParam(param.name, arg, b)
        }

      case _ => super.transform(tree)
    }
  }.transform(function)

  def apply(function: Tree):Tree =
    c.untypecheck(
      inline(function)
    )

  def inlineAndReset[T](tree: Tree): c.Expr[T] = {
    c.Expr[T](apply(tree))
  }
}
