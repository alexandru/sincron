package scalax.concurrent.cancelables

import scalax.concurrent.misc.Unsafe

/** Optimized implementation of a simple [[BooleanCancelable]],
  * a cancelable that doesn't trigger any actions on cancel, it just
  * mutates its internal `isCanceled` field, which might be visible
  * at some point.
  *
  * Its `cancel()` always returns false, always.
  */
private[cancelables] class WeakBooleanCancelable private (fieldOffset: Long)
  extends BooleanCancelable {

  private[this] var _isCanceled = 0
  def isCanceled: Boolean = {
    _isCanceled == 1
  }

  def cancel(): Boolean = {
    if (_isCanceled == 0)
      Unsafe.putOrderedInt(this, fieldOffset, 1)
    false
  }
}

private[cancelables] object WeakBooleanCancelable {
  /** Builder for [[WeakBooleanCancelable]] */
  def apply(): BooleanCancelable = {
    new WeakBooleanCancelable(addressOffset)
  }

  /** Address offset of `SimpleBooleanCancelable#_isCanceled`,
    * needed in order to use `Unsafe.putOrderedInt`.
    */
  private[this] val addressOffset: Long = {
    Unsafe.objectFieldOffset(classOf[WeakBooleanCancelable]
      .getDeclaredFields.find(_.getName.endsWith("_isCanceled")).get)
  }
}