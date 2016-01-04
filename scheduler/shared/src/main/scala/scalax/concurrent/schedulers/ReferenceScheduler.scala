package scalax.concurrent.schedulers

import java.util.concurrent.TimeUnit
import scalax.concurrent.{Scheduler, Cancelable}
import scalax.concurrent.cancelables.MultiAssignmentCancelable

/** Helper for building a [[Scheduler]].
  *
  * You can inherit from this class and provided a correct
  * [[Scheduler.scheduleOnce(initialDelay* scheduleOnce]]
  * you'll get [[Scheduler.scheduleWithFixedDelay]] and
  * [[Scheduler.scheduleAtFixedRate]] for free.
  */
private[schedulers] abstract class ReferenceScheduler extends Scheduler {
  override def currentTimeMillis(): Long =
    System.currentTimeMillis()

  override def scheduleWithFixedDelay(initialDelay: Long, delay: Long, unit: TimeUnit, r: Runnable): Cancelable = {
    val sub = MultiAssignmentCancelable()

    def loop(initialDelay: Long, delay: Long): Unit =
      sub := scheduleOnce(initialDelay, unit, new Runnable {
        def run(): Unit = {
          r.run()
          loop(delay, delay)
        }
      })

    loop(initialDelay, delay)
    sub
  }

  override def scheduleAtFixedRate(initialDelay: Long, period: Long, unit: TimeUnit, r: Runnable): Cancelable = {
    val sub = MultiAssignmentCancelable()

    def loop(initialDelayMs: Long, periodMs: Long): Unit = {
      val startedAtMillis = currentTimeMillis()

      sub := scheduleOnce(initialDelayMs, TimeUnit.MILLISECONDS, new Runnable {
        def run(): Unit = {
          r.run()

          val delay = {
            val durationMillis = currentTimeMillis() - startedAtMillis
            val d = periodMs - durationMillis
            if (d >= 0) d else 0
          }

          loop(delay, periodMs)
        }
      })
    }

    val initialMs = TimeUnit.MILLISECONDS.convert(initialDelay, unit)
    val periodMs = TimeUnit.MILLISECONDS.convert(period, unit)

    loop(initialMs, periodMs)
    sub
  }

  /** Runs a block of code in this `ExecutionContext`. */
  def execute(runnable: Runnable): Unit

  /** Reports that an asynchronous computation failed. */
  def reportFailure(t: Throwable): Unit
}
