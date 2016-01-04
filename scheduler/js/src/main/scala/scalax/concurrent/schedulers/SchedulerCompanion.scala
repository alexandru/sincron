package scalax.concurrent.schedulers

import scalax.concurrent.{Scheduler, UncaughtExceptionReporter}
import scalax.concurrent.UncaughtExceptionReporter.LogExceptionsToStandardErr

private[concurrent] abstract class SchedulerCompanion {
  /**
    * [[Scheduler]] builder.
    *
    * @param reporter is the [[UncaughtExceptionReporter]] that logs uncaught exceptions.
    */
  def apply(reporter: UncaughtExceptionReporter = LogExceptionsToStandardErr): Scheduler = {
    AsyncScheduler(reporter)
  }

  /**
    * Builds a [[scalax.concurrent.schedulers.TrampolineScheduler TrampolineScheduler]].
    *
    * @param reporter is the [[UncaughtExceptionReporter]] that logs uncaught exceptions.
    */
  def trampoline(reporter: UncaughtExceptionReporter = LogExceptionsToStandardErr): Scheduler = {
    TrampolineScheduler(reporter)
  }

  /** The explicit global `Scheduler`. Invoke `global` when you want to provide the global
    * `Scheduler` explicitly.
    */
  def global: Scheduler = Implicits.global

  object Implicits {
    /**
      * A global [[Scheduler]] instance, provided for convenience, piggy-backing
      * on top of `global.setTimeout`.
      */
    implicit lazy val global: Scheduler =
      AsyncScheduler(UncaughtExceptionReporter.LogExceptionsToStandardErr)
  }
}