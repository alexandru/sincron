package scalax.concurrent.schedulers

import scalax.concurrent.UncaughtExceptionReporter
import scala.scalajs.js

/** Utils for quickly using Javascript's `setTimeout` and
  * `clearTimeout`.
  */
private[schedulers] object Timer {
  def setTimeout(delayMillis: Long, r: Runnable, reporter: UncaughtExceptionReporter): js.Dynamic = {
    val lambda: js.Function = () =>
      try { r.run() } catch { case t: Throwable =>
        reporter.reportFailure(t)
      }

    js.Dynamic.global.setTimeout(lambda, delayMillis)
  }

  def clearTimeout(task: js.Dynamic) = {
    js.Dynamic.global.clearTimeout(task)
  }
}
