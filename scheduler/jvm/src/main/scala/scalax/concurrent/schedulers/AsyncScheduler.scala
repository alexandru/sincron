package scalax.concurrent.schedulers

import java.util.concurrent.{TimeUnit, ScheduledExecutorService}
import scala.concurrent.ExecutionContext
import scalax.concurrent.cancelables.BooleanCancelable
import scalax.concurrent.{Cancelable, UncaughtExceptionReporter}

/** An `AsyncScheduler` schedules tasks to happen in the future with the
  * given `ScheduledExecutorService` and the tasks themselves are executed on
  * the given `ExecutionContext`.
  */
private[schedulers] final class AsyncScheduler private
  (s: ScheduledExecutorService, ec: ExecutionContext, r: UncaughtExceptionReporter)
  extends ReferenceScheduler {

  override def scheduleOnce(r: Runnable): Cancelable = {
    val cancelable = BooleanCancelable.weak()
    val wrapped = new Runnable { def run() = if (!cancelable.isCanceled) r.run() }
    execute(wrapped)
    cancelable
  }

  override def scheduleOnce(initialDelay: Long, unit: TimeUnit, r: Runnable) = {
    if (initialDelay <= 0)
      scheduleOnce(r)
    else {
      val task = s.schedule(r, initialDelay, unit)
      Cancelable(task.cancel(true))
    }
  }

  override def scheduleWithFixedDelay(initialDelay: Long, delay: Long, unit: TimeUnit, r: Runnable): Cancelable = {
    val task = s.scheduleWithFixedDelay(r, initialDelay, delay, unit)
    Cancelable(task.cancel(false))
  }

  override def scheduleAtFixedRate(initialDelay: Long, period: Long, unit: TimeUnit, r: Runnable): Cancelable = {
    val task = s.scheduleAtFixedRate(r, initialDelay, period, unit)
    Cancelable(task.cancel(false))
  }

  override def execute(runnable: Runnable): Unit =
    ec.execute(runnable)

  override def reportFailure(t: Throwable): Unit =
    r.reportFailure(t)
}

private[schedulers] object AsyncScheduler {
  def apply(schedulerService: ScheduledExecutorService,
    ec: ExecutionContext, reporter: UncaughtExceptionReporter): AsyncScheduler =
    new AsyncScheduler(schedulerService, ec, reporter)
}
