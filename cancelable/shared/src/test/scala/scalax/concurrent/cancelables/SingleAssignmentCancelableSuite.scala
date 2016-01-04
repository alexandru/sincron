package scalax.concurrent.cancelables

import minitest.SimpleTestSuite
import scalax.concurrent.Cancelable

object SingleAssignmentCancelableSuite extends SimpleTestSuite {
  test("cancel()") {
    var effect = 0
    val s = SingleAssignmentCancelable()
    val b = BooleanCancelable { effect += 1 }
    s := b

    s.cancel()
    assert(s.isCanceled)
    assert(b.isCanceled)
    assert(effect == 1)

    s.cancel()
    assert(effect == 1)
  }

  test("cancel on single assignment") {
    val s = SingleAssignmentCancelable()
    s.cancel()
    assert(s.isCanceled)

    var effect = 0
    val b = BooleanCancelable { effect += 1 }
    s := b

    assert(b.isCanceled)
    assert(effect == 1)

    s.cancel()
    assert(effect == 1)
  }

  test("throw exception on multi assignment") {
    val s = SingleAssignmentCancelable()
    val b1 = Cancelable()
    s := b1

    intercept[IllegalStateException] {
      val b2 = Cancelable()
      s := b2
    }
  }

  test("throw exception on multi assignment when canceled") {
    val s = SingleAssignmentCancelable()
    s.cancel()

    val b1 = Cancelable()
    s := b1

    intercept[IllegalStateException] {
      val b2 = Cancelable()
      s := b2
    }
  }
}
