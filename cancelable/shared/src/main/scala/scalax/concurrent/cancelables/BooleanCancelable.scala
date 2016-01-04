package scalax.concurrent.cancelables

import scalax.concurrent.Cancelable
import scalax.concurrent.atomic.padded.Atomic

/**
  * Represents a Cancelable that can queried for the canceled status.
  */
trait BooleanCancelable extends Cancelable {
  /** @return true in case this cancelable hasn't been canceled,
    *         or false otherwise.
    */
  def isCanceled: Boolean
}

object BooleanCancelable {
  /** Builder for [[BooleanCancelable]] */
  def apply(): BooleanCancelable =
    new BooleanCancelable {
      private[this] val _isCanceled = Atomic(false)
      def isCanceled = _isCanceled.get

      def cancel(): Boolean = {
        _isCanceled.compareAndSet(expect = false, update = true)
      }
    }

  /** Builder for [[BooleanCancelable]].
    *
    * @param callback is a function that will execute exactly once
    *                 on canceling.
    */
  def apply(callback: => Unit): BooleanCancelable =
    new BooleanCancelable {
      private[this] val _isCanceled = Atomic(false)

      def isCanceled =
        _isCanceled.get

      def cancel(): Boolean = {
        if (_isCanceled.compareAndSet(expect=false, update=true)) {
          callback
          true
        }
        else
          false
      }
    }

  /** Returns a weak version of [[BooleanCancelable]] that:
    *
    *   - always returns false on `cancel()`
    *   - its `isCanceled` field only has the writes ordered, so
    *     visibility after cancellation is not guaranteed
    */
  def weak(): BooleanCancelable =
    scalax.concurrent.cancelables.WeakBooleanCancelable()

  /** Returns an instance of a [[BooleanCancelable]] that's
    * already canceled.
    */
  val alreadyCanceled: BooleanCancelable =
    new BooleanCancelable {
      val isCanceled = true
      def cancel() = false
    }
}
