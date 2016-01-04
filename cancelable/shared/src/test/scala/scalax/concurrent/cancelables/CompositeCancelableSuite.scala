package scalax.concurrent.cancelables

import minitest.SimpleTestSuite

object CompositeCancelableSuite extends SimpleTestSuite {
  test("cancel") {
    val s = CompositeCancelable()
    val b1 = BooleanCancelable()
    val b2 = BooleanCancelable()
    s += b1
    s += b2
    s.cancel()

    assert(s.isCanceled)
    assert(b1.isCanceled)
    assert(b2.isCanceled)
  }

  test("cancel on assignment after being canceled") {
    val s = CompositeCancelable()
    val b1 = BooleanCancelable()
    s += b1
    s.cancel()

    val b2 = BooleanCancelable()
    s += b2

    assert(s.isCanceled)
    assert(b1.isCanceled)
    assert(b2.isCanceled)
  }
}
