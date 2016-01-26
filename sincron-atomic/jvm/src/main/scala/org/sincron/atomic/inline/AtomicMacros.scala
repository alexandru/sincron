package org.sincron.atomic.inline

import org.sincron.atomic.Atomic
import org.sincron.atomic.inline.compat._

object AtomicMacros {
  def transformMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })(cb: c.Expr[T => T]): c.Expr[Unit] = {
    import c.universe._
    val util = SyntaxUtil[c.type](c)
    val selfExpr: c.Expr[Atomic[T]] = c.prefix

    val self = util.name("self")
    val current = util.name("current")
    val update = util.name("update")

    /*
     * If our arguments are all "clean" (anonymous functions or simple
     * identifiers) then we can go ahead and just inline them directly
     * into a while loop.
     *
     * If one or more of our arguments are "dirty" (something more
     * complex than an anonymous function or simple identifier) then
     * we will go ahead and bind each argument to a val just to be
     * safe.
     */
    val tree =
      q"""
        val $self = $selfExpr
        var $current = $self.get
        var $update = $cb($current)

        while (!$self.compareAndSet($current, $update)) {
          $current = $self.get
          $update = $cb($current)
        }
      """

    new InlineUtil[c.type](c).inlineAndReset[Unit](tree)
  }

  def transformAndGetMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })(cb: c.Expr[T => T]): c.Expr[T] = {
    import c.universe._
    val util = SyntaxUtil[c.type](c)
    val selfExpr: c.Expr[Atomic[T]] = c.prefix

    val self = util.name("self")
    val current = util.name("current")
    val update = util.name("update")

    /*
     * If our arguments are all "clean" (anonymous functions or simple
     * identifiers) then we can go ahead and just inline them directly
     * into a while loop.
     *
     * If one or more of our arguments are "dirty" (something more
     * complex than an anonymous function or simple identifier) then
     * we will go ahead and bind each argument to a val just to be
     * safe.
     */
    val tree =
      q"""
        val $self = $selfExpr
        var $current = $self.get
        var $update = $cb($current)

        while (!$self.compareAndSet($current, $update)) {
          $current = $self.get
          $update = $cb($current)
        }

        $update
      """

    new InlineUtil[c.type](c).inlineAndReset[T](tree)
  }

  def getAndTransformMacro[T : c.WeakTypeTag](c: Context { type PrefixType = Atomic[T] })(cb: c.Expr[T => T]): c.Expr[T] = {
    import c.universe._
    val util = SyntaxUtil[c.type](c)
    val selfExpr: c.Expr[Atomic[T]] = c.prefix

    val self = util.name("self")
    val current = util.name("current")
    val update = util.name("update")

    /*
     * If our arguments are all "clean" (anonymous functions or simple
     * identifiers) then we can go ahead and just inline them directly
     * into a while loop.
     *
     * If one or more of our arguments are "dirty" (something more
     * complex than an anonymous function or simple identifier) then
     * we will go ahead and bind each argument to a val just to be
     * safe.
     */
    val tree =
      q"""
        val $self = $selfExpr
        var $current = $self.get
        var $update = $cb($current)

        while (!$self.compareAndSet($current, $update)) {
          $current = $self.get
          $update = $cb($current)
        }

        $current
      """

    new InlineUtil[c.type](c).inlineAndReset[T](tree)
  }

  def transformAndExtractMacro[S : c.WeakTypeTag, A : c.WeakTypeTag]
  (c: Context { type PrefixType = Atomic[S] })
    (cb: c.Expr[S => (A, S)]): c.Expr[A] = {

    import c.universe._
    val util = SyntaxUtil[c.type](c)
    val selfExpr: c.Expr[Atomic[S]] = c.prefix

    val self = util.name("self")
    val current = util.name("current")
    val updateVar = util.name("updateVar")
    val resultVar = util.name("resultVar")
    val updateTmp = util.name("updateTmp")
    val resultTmp = util.name("resultTmp")

    /*
     * If our arguments are all "clean" (anonymous functions or simple
     * identifiers) then we can go ahead and just inline them directly
     * into a while loop.
     *
     * If one or more of our arguments are "dirty" (something more
     * complex than an anonymous function or simple identifier) then
     * we will go ahead and bind each argument to a val just to be
     * safe.
     */
    val tree =
      q"""
        val $self = $selfExpr
        var $current = $self.get
        var ($resultVar, $updateVar) = $cb($current)

        while (!$self.compareAndSet($current, $updateVar)) {
          $current = $self.get
          val ($resultTmp, $updateTmp) = $cb($current)
          $updateVar = $updateTmp
          $resultVar = $resultTmp
        }

        $resultVar
      """

    new InlineUtil[c.type](c).inlineAndReset[A](tree)
  }
}
