package scalax.concurrent

import minitest.TestSuite
import scalax.concurrent.FutureUtils.ops._
import scalax.concurrent.schedulers.TestScheduler
import scala.concurrent.duration._
import scala.concurrent.{Future, TimeoutException}

object FutureUtilsSuite extends TestSuite[TestScheduler] {
  def setup() = TestScheduler()

  def tearDown(env: TestScheduler): Unit = {
    assert(env.state.get.tasks.isEmpty, "should not have tasks left to execute")
  }

  test("delayedResult") { implicit s =>
    val f = Future.delayedResult(100.millis)("TICK")

    s.tick(50.millis)
    assert(!f.isCompleted)

    s.tick(100.millis)
    assert(f.value.get.get == "TICK")
  }

  test("withTimeout should succeed") { implicit s =>
    val f = Future.delayedResult(50.millis)("Hello world!")
    val t = f.timeout(300.millis)

    s.tick(10.seconds)
    assert(t.value.get.get == "Hello world!")
  }

  test("withTimeout should fail") { implicit s =>
    val f = Future.delayedResult(1.second)("Hello world!")
    val t = f.timeout(30.millis)

    s.tick(10.seconds)
    intercept[TimeoutException](t.value.get.get)
  }
}
