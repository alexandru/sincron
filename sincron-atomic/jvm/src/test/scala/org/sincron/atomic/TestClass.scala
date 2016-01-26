package org.sincron.atomic

class TestClass {
  def pula(): Int = {
    val value = Atomic(1)
    value.transformAndGet(_ + 1)
  }
}
