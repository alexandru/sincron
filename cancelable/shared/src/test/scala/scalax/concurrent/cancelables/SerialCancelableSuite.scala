package scalax.concurrent.cancelables

import minitest.SimpleTestSuite

object SerialCancelableSuite extends SimpleTestSuite {
  test("cancel()") {
    var effect = 0
    val sub = BooleanCancelable(effect += 1)
    val mSub = SerialCancelable(sub)

    assert(effect == 0)
    assert(!sub.isCanceled)
    assert(!mSub.isCanceled)

    mSub.cancel()
    assert(sub.isCanceled && mSub.isCanceled)
    assert(effect == 1)

    mSub.cancel()
    assert(sub.isCanceled && mSub.isCanceled)
    assert(effect == 1)
  }

  test("cancel() after second assignment") {
    var effect = 0
    val sub = BooleanCancelable(effect += 1)
    val mSub = SerialCancelable(sub)
    val sub2 = BooleanCancelable(effect += 10)
    mSub() = sub2

    assert(effect == 1)
    assert(sub.isCanceled && !sub2.isCanceled && !mSub.isCanceled)

    mSub.cancel()
    assert(sub2.isCanceled && mSub.isCanceled && sub.isCanceled)
    assertEquals(effect, 11)
  }

  test("automatically cancel assigned") {
    val mSub = SerialCancelable()
    mSub.cancel()

    var effect = 0
    val sub = BooleanCancelable(effect += 1)

    assert(effect == 0)
    assert(!sub.isCanceled && mSub.isCanceled)

    mSub() = sub
    assert(effect == 1)
    assert(sub.isCanceled)
  }
}
