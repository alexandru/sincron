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

import org.sincron.macros.compat.setOrig
import scala.annotation.tailrec
import scala.language.higherKinds
import scala.reflect.macros.whitebox

@macrocompat.bundle
trait InlineMacros {
  val c: whitebox.Context

  import c.universe._

  def inlineAndReset[T](tree: Tree): c.Expr[T] = {
    c.Expr[T](inlineAndResetTree(tree))
  }

  val ApplyName = TermName("apply")


  def inlineAndResetTree(tree: Tree): c.Tree = {
    // Workaround for https://issues.scala-lang.org/browse/SI-5465
    class StripUnApplyNodes extends Transformer {
      val global = c.universe.asInstanceOf[scala.tools.nsc.Global]
      import global.nme

      override def transform(tree: Tree): Tree = {
        super.transform {
          tree match {
            case UnApply(Apply(Select(qualifier, nme.unapply | nme.unapplySeq), List(Ident(nme.SELECTOR_DUMMY))), args) =>
              Apply(transform(qualifier), transformTrees(args))
            case UnApply(Apply(TypeApply(Select(qualifier, nme.unapply | nme.unapplySeq), _), List(Ident(nme.SELECTOR_DUMMY))), args) =>
              Apply(transform(qualifier), transformTrees(args))
            case t => t
          }
        }
      }
    }

    val inlined = inlineApplyRecursive(tree)
    val fixed = fixup(inlined)
    val tupleCleaned = Cleanup.transform(fixed)
    val clean = c.untypecheck(tupleCleaned)
    new StripUnApplyNodes().transform(clean)
  }

  def inlineApplyRecursive(tree: Tree): Tree = {

    class InlineSymbol(symbol: TermName, value: Tree) extends Transformer {
      override def transform(tree: Tree): Tree = tree match {
        case i@Ident(_) if i.name == symbol =>
          value
        case tt: TypeTree if tt.original != null =>
          //super.transform(TypeTree().setOriginal(transform(tt.original)))
          super.transform(setOrig(c)(TypeTree(), transform(tt.original)))
        case _ =>
          super.transform(tree)
      }
    }

    object InlineApply extends Transformer {
      def inlineSymbol(symbol: TermName, body: Tree, arg: Tree): Tree =
        new InlineSymbol(symbol, arg).transform(body)

      override def transform(tree: Tree): Tree = tree match {
        case Apply(Select(Function(params, body), ApplyName), args) =>
          params.zip(args).foldLeft(body) { case (b, (param, arg)) =>
            inlineSymbol(param.name, b, arg)
          }

        case Apply(Function(params, body), args) =>
          params.zip(args).foldLeft(body) { case (b, (param, arg)) =>
            inlineSymbol(param.name, b, arg)
          }

        case _ =>
          super.transform(tree)
      }
    }

    InlineApply.transform(tree)
  }

  /**
    * Given the following code:
    *
    *   val t = Tuple6#apply(a0, b0, ...f0)
    *   val a = t._1
    *   val b = t._2
    *   ...
    *   val f = t._6
    *
    * This transformer rewrites it to avoid tuple construction:
    *
    *   val a = a0
    *   val b = b0
    *   ...
    *   val f = f0
    *
    * It should work for tuples of any size.
    */
  object Fixer extends Transformer {

    /**
      * Check the given list of trees to see if they represent a series
      * of tuple access. Return either None (if they don't) or a Some
      * tuple of the argument names, and the remaining trees (that were
      * not part of the tuple assignments).
      */
    def matchTplDerefs(n: Int, tplName: String, trees: List[Tree]): Option[(List[String], List[Tree])] = {
      @tailrec
      def loop(i: Int, ns: List[String], ts: List[Tree]): Option[(List[String], List[Tree])] = {
        if (i < n) {
          val tplGetter = "_" + (i + 1).toString
          ts match {
            case Nil =>
              None
            case ValDef(_, TermName(s), _,
            Select(Ident(TermName(tplName)), TermName(`tplGetter`))) :: tail =>
              loop(i + 1, s :: ns, tail)
          }
        } else {
          Some((ns.reverse, ts))
        }
      }
      loop(0, Nil, trees)
    }

    def handle(trees: List[Tree]): List[Tree] = trees match {
      case ValDef(
        _: Modifiers, TermName(xx), _,
        Match(
          Annotated(Apply(Select(New(Ident(_)), _), Nil),
            Apply(TypeApply(Select(Select(Ident(scala), TermName(tn)), ApplyName), _),
              args)), _)) :: rest if tn.startsWith("Tuple") =>

        matchTplDerefs(args.length, tn, rest) match {
          case Some((names, rest)) =>
            names.zip(args).map { case (name, arg) =>
              ValDef(Modifiers(), TermName(name), TypeTree(), arg): Tree
            } ::: handle(rest)
          case None =>
            trees.head :: handle(rest)
        }

      case t :: ts =>
        t :: handle(ts)

      case Nil =>
        Nil
    }

    override def transform(tree: Tree): Tree = tree match {
      case Block(trees, last) =>
        super.transform(Block(handle(trees), last))

      case _ =>
        super.transform(tree)
    }
  }

  /**
    * This transformer is designed to clean up extra matching and
    * Typed detritus that may otherwise derail inlining.
    *
    * TODO: Figure out why inliner doesn't rewrite Typed nodes.
    *
    * TODO: See if we need similar clean up for Tuple2.
    */
  object Cleanup extends Transformer {
    override def transform(tree: Tree): Tree = tree match {
      case Match(
        body,
        List(
          CaseDef(Apply(Ident(TermName("Tuple3")), List(
            Bind(TermName("b2"), Ident(termNames.WILDCARD)),
            Bind(TermName("c2"), Ident(termNames.WILDCARD)),
            Bind(TermName("d2"), Ident(termNames.WILDCARD))
          )), EmptyTree,
          Apply(Select(Ident(TermName("Tuple3")), ApplyName), List(
            Ident(TermName("b2")),
            Ident(TermName("c2")),
            Ident(TermName("d2"))
        ))))) =>
          transform(body)

      case Match(
        body,
        List(
          CaseDef(Apply(Ident(TermName("Tuple2")), List(
            Bind(TermName("b2"), Ident(termNames.WILDCARD)),
            Bind(TermName("c2"), Ident(termNames.WILDCARD))
          )), EmptyTree,
          Apply(Select(Ident(TermName("Tuple2")), ApplyName), List(
            Ident(TermName("b2")),
            Ident(TermName("c2"))
          ))))) =>
            transform(body)

      case Typed(y, z) => y

      case _ =>
        super.transform(tree)
    }
  }

  def fixup(tree: Tree): Tree = Fixer.transform(tree)

}
