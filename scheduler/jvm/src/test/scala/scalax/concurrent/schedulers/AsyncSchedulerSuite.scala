package scalax.concurrent.schedulers

import java.util.concurrent.{CountDownLatch, TimeUnit, TimeoutException}
import minitest.SimpleTestSuite
import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._
import scalax.concurrent.cancelables.{BooleanCancelable, SingleAssignmentCancelable}
import scalax.concurrent.{Scheduler, Cancelable}

object AsyncSchedulerSuite extends SimpleTestSuite {
  val s = scalax.concurrent.Scheduler.global

  def scheduleOnce(s: Scheduler, delay: FiniteDuration)(action: => Unit): Cancelable = {
    s.scheduleOnce(delay.length, delay.unit, runnableAction(action))
  }

  test("scheduleOnce with delay") {
    val p = Promise[Long]()
    val startedAt = System.nanoTime()
    scheduleOnce(s, 100.millis)(p.success(System.nanoTime()))

    val timeTaken = Await.result(p.future, 3.second)
    assert((timeTaken - startedAt).nanos.toMillis >= 100)
  }

  test("scheduleOnce with delay lower than 1.milli") {
    val p = Promise[Int]()
    scheduleOnce(s, 20.nanos)(p.success(1))
    assert(Await.result(p.future, 3.seconds) == 1)
  }

  test("scheduleOnce with delay and cancel") {
    val p = Promise[Int]()
    val task = scheduleOnce(s, 100.millis)(p.success(1))
    task.cancel()

    intercept[TimeoutException] {
      Await.result(p.future, 150.millis)
    }
  }

  test("schedule with fixed delay") {
    val sub = SingleAssignmentCancelable()
    val p = Promise[Int]()
    var value = 0

    sub := s.scheduleWithFixedDelay(10, 50, TimeUnit.MILLISECONDS, runnableAction {
      if (value + 1 == 4) {
        value += 1
        sub.cancel()
        p.success(value)
      }
      else if (value < 4) {
        value += 1
      }
    })

    assert(Await.result(p.future, 5.second) == 4)
  }

  test("schedule at fixed rate") {
    val sub = SingleAssignmentCancelable()
    val p = Promise[Int]()
    var value = 0

    sub := s.scheduleAtFixedRate(10, 50, TimeUnit.MILLISECONDS, runnableAction {
      if (value + 1 == 4) {
        value += 1
        sub.cancel()
        p.success(value)
      }
      else if (value < 4) {
        value += 1
      }
    })

    assert(Await.result(p.future, 5.second) == 4)
  }

  test("scheduleOnce simple runnable") {
    val latch = new CountDownLatch(1)
    s.scheduleOnce(runnableAction {
      latch.countDown()
    })

    assert(latch.await(10, TimeUnit.SECONDS), "latch.await")
  }

  test("scheduleOnce with simple runnable should cancel") {
    val s = Scheduler.singleThread("single-threaded-test")
    val started = new CountDownLatch(1)
    val continue = new CountDownLatch(1)
    val wasTriggered = new CountDownLatch(1)

    s.scheduleOnce(runnableAction {
      started.countDown()
      // block our thread
      continue.await()
    })

    assert(started.await(10, TimeUnit.SECONDS), "started.await")
    val cancelable = s.scheduleOnce(runnableAction {
      wasTriggered.countDown()
    })

    cancelable.cancel()
    assert(cancelable.asInstanceOf[BooleanCancelable].isCanceled, "cancelable.isCanceled")
    continue.countDown()

    assert(!wasTriggered.await(100, TimeUnit.MILLISECONDS))
  }

  def runnableAction(f: => Unit): Runnable =
    new Runnable { def run() = f }
}
