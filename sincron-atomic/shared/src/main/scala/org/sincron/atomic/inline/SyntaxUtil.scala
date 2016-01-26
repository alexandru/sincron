package org.sincron.atomic.inline

import org.sincron.atomic.inline.compat._

case class SyntaxUtil[C <: Context with Singleton](val c: C) {
  import c.universe._

  def name(s: String) = freshTermName(c)(s + "$")

  def names(bs: String*) = bs.toList.map(name)

  def isClean(es: c.Expr[_]*): Boolean =
    es.forall {
      _.tree match {
        case t @ Ident(_: TermName) if t.symbol.asTerm.isStable => true
        case Function(_, _) => true
        case _ => false
      }
    }
}
