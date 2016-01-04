package scalax.concurrent.cancelables

/** Optimized implementation of a simple [[BooleanCancelable]],
  * a cancelable that doesn't trigger any actions on cancel, it just
  * mutates its internal `isCanceled` field, which might be visible
  * at some point.
  *
  * Its `cancel()` always returns false, always.
  */
private[cancelables] class WeakBooleanCancelable private ()
  extends BooleanCancelable {

  private[this] var _isCanceled = false
  def isCanceled = _isCanceled

  def cancel(): Boolean = {
    _isCanceled = true
    false
  }
}

private[cancelables] object WeakBooleanCancelable {
  def apply(): BooleanCancelable =
    new WeakBooleanCancelable
}