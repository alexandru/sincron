package scalax.concurrent.schedulers

import java.util.concurrent.TimeUnit
import scalax.concurrent.schedulers.Timer.{clearTimeout, setTimeout}
import scalax.concurrent.{Cancelable, UncaughtExceptionReporter}

/**
  * An `AsyncScheduler` schedules tasks to happen in the future with
  * the given `ScheduledExecutorService` and the tasks themselves are
  * executed on the given `ExecutionContext`.
  */
private[schedulers] final class AsyncScheduler private (reporter: UncaughtExceptionReporter)
  extends ReferenceScheduler {

  override def scheduleOnce(r: Runnable): Cancelable = {
    val task = setTimeout(0, r, reporter)
    Cancelable(clearTimeout(task))
  }

  override def scheduleOnce(initialDelay: Long, unit: TimeUnit, r: Runnable) = {
    val millis = {
      val v = TimeUnit.MILLISECONDS.convert(initialDelay, unit)
      if (v < 0) 0L else v
    }

    val task = setTimeout(millis, r, reporter)
    Cancelable(clearTimeout(task))
  }

  override def execute(runnable: Runnable): Unit = {
    setTimeout(0L, runnable, reporter)
  }

  override def reportFailure(t: Throwable): Unit =
    reporter.reportFailure(t)
}

private[schedulers] object AsyncScheduler {
  def apply(reporter: UncaughtExceptionReporter): AsyncScheduler =
    new AsyncScheduler(reporter)
}

