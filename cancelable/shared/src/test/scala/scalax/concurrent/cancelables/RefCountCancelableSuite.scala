package scalax.concurrent.cancelables

import minitest.SimpleTestSuite

object RefCountCancelableSuite extends SimpleTestSuite {
  test("cancel without dependent references") {
    var isCanceled = false
    val sub = RefCountCancelable { isCanceled = true }
    sub.cancel()

    assert(isCanceled)
  }

  test("execute onCancel with no dependent refs active") {
    var isCanceled = false
    val sub = RefCountCancelable { isCanceled = true }

    val s1 = sub.acquire()
    val s2 = sub.acquire()
    s1.cancel()
    s2.cancel()

    assert(!isCanceled)
    assert(!sub.isCanceled)

    sub.cancel()

    assert(isCanceled)
    assert(sub.isCanceled)
  }

  test("execute onCancel only after all dependent refs have been canceled") {
    var isCanceled = false
    val sub = RefCountCancelable { isCanceled = true }

    val s1 = sub.acquire()
    val s2 = sub.acquire()
    sub.cancel()

    assert(sub.isCanceled)
    assert(!isCanceled)
    s1.cancel()
    assert(!isCanceled)
    s2.cancel()
    assert(isCanceled)
  }
}
