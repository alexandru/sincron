---
layout: page
title: Macro Utilities
permalink: /tut/base/macros.html
---

Sincron exposes utilities for inlining function arguments in macros,
in a way that's compatible with both Scala 2.10 and 2.11.

Suppose we've got this type:

```tut:silent
case class Box[T](value: T) {
  def map[U](f: T => U): Box[U] = ???
}
```

In order to build a `map` macro that inlines the given function `f`, 
we can do this:

```tut:silent
object Playground {  
  // Hack to support both Scala 2.10 and Scala 2.11
  import org.sincron.macros.compat._
  import org.sincron.macros.{SyntaxUtil, InlineUtil}
  import scala.language.experimental.macros
  
  case class Box[T](val value: T) extends AnyVal {
    def transformAndGet[U](f: T => U): U = 
      macro Box.transformAndGetMacro[T,U]
  }

  object Box {
    /** Macro implementation for [[Box.map]] */
    def transformAndGetMacro[T : c.WeakTypeTag, U : c.WeakTypeTag]
      (c: Context { type PrefixType = Box[T] })
      (f: c.Expr[T => U]): c.Expr[U] = {

      import c.universe._
      val util = SyntaxUtil[c.type](c)
      val selfExpr: c.Expr[Box[T]] = c.prefix

      /*
       * If our arguments are all "clean" (anonymous functions or simple
       * identifiers) then we can go ahead and just inline them directly.
       *
       * If one or more of our arguments are "dirty" (something more
       * complex than an anonymous function or simple identifier) then
       * we will go ahead and bind each argument to a val just to be
       * safe.
       */
      val tree =
        if (util.isClean(selfExpr, f)) {
          q"""
          $f($selfExpr.value)
          """
        }
        else {
          val self = util.name("self")
          val fn = util.name("fn")

          q"""
          val $self = $selfExpr
          val $fn = $f
          $fn($self.value)
          """
        }

      new InlineUtil[c.type](c).inlineAndReset[U](tree)
    }
  }
}
```

In order to check that the inlining works, define another class like so:

```tut
class Test { 
  def test() = { Playground.Box(1).transformAndGet(_+1) } 
}
```


As evidence you can use the `javap` utility to check the generated code (and it's a good
idea to do so).  From SBT's `console` you can invoke `javap` directly, like this:

```
:javap Test
```
 
What this will give us is more or less:

```
public int test();
  descriptor: ()I
  flags: ACC_PUBLIC
  Code:
    stack=2, locals=2, args_size=1
       0: iconst_1
       1: invokestatic  #13                 // Method scala/runtime/BoxesRunTime.boxToInteger:(I)Ljava/lang/Integer;
       4: checkcast     #15                 // class java/lang/Integer
       7: astore_1
       8: aload_1
       9: invokestatic  #19                 // Method scala/runtime/BoxesRunTime.unboxToInt:(Ljava/lang/Object;)I
      12: iconst_1
      13: iadd
      14: ireturn
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0      15     0  this   LTest;
          8       6     1 self$macro$73   Ljava/lang/Integer;
    LineNumberTable:
      line 41: 0
```

If you'll take a look in the above `javap` dump, you'll see `iadd` on line 13, instead
of seeing some anonymous function being initialized and applied. 