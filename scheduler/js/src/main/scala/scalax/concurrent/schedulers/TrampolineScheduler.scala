package scalax.concurrent.schedulers

import java.util.concurrent.TimeUnit
import scalax.concurrent.schedulers.Timer.{clearTimeout, setTimeout}
import scalax.concurrent.{Cancelable, UncaughtExceptionReporter}
import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.duration.TimeUnit
import scala.util.control.NonFatal

private[schedulers] final class TrampolineScheduler private (reporter: UncaughtExceptionReporter)
  extends ReferenceScheduler {

  private[this] val immediateQueue = mutable.Queue.empty[Runnable]
  private[this] var withinLoop = false

  override def scheduleOnce(r: Runnable): Cancelable = {
    execute(r)
    Cancelable.empty
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
    immediateQueue.enqueue(runnable)
    if (!withinLoop) {
      withinLoop = true
      try immediateLoop() finally {
        withinLoop = false
      }
    }
  }

  @tailrec
  private[this] def immediateLoop(): Unit = {
    if (immediateQueue.nonEmpty) {
      val task = immediateQueue.dequeue()

      try {
        task.run()
      }
      catch {
        case NonFatal(ex) =>
          reportFailure(ex)
      }

      immediateLoop()
    }
  }

  override def reportFailure(t: Throwable): Unit =
    reporter.reportFailure(t)
}

private[schedulers] object TrampolineScheduler {
  def apply(reporter: UncaughtExceptionReporter): TrampolineScheduler =
    new TrampolineScheduler(reporter)
}
