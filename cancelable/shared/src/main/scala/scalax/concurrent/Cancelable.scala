package scalax.concurrent

import scalax.concurrent.atomic.padded.Atomic

/**
  * Represents an asynchronous computation whose execution can be canceled.
  *
  * It is equivalent to `java.io.Closeable`, but without the I/O focus, or
  * to `IDisposable` in Microsoft .NET, or to `akka.actor.Cancellable`.
  *
  * @see [[scalax.concurrent.cancelables]]
  */
trait Cancelable {
  /** Cancels the unit of work represented by this reference.
    *
    * Guaranteed idempotency - calling it multiple times should have the
    * same side-effect as calling it only a single time. Implementations
    * of this method should also be thread-safe.
    *
    * @return true if cancellation happened, or false if another execution
    *         happened previously or concurrently.
    */
  def cancel(): Boolean
}

object Cancelable {
  def apply(callback: => Unit): Cancelable =
    new Cancelable {
      private[this] val _isCanceled = Atomic(false)

      def cancel(): Boolean = {
        if (_isCanceled.compareAndSet(expect=false, update=true)) {
          callback
          true
        }
        else
          false
      }
    }

  def apply(): Cancelable =
    new Cancelable {
      private[this] val _isCanceled = Atomic(false)

      def cancel(): Boolean =
        _isCanceled.compareAndSet(expect = false, update = true)
    }

  val empty: Cancelable =
    new Cancelable {
      def cancel() = false
    }
}
