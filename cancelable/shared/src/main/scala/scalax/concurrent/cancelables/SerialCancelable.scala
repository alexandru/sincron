package scalax.concurrent.cancelables

import scalax.concurrent.Cancelable
import scalax.concurrent.atomic.Atomic
import scala.annotation.tailrec

/**
 * Represents a [[scalax.concurrent.Cancelable]] whose underlying cancelable
 * can be swapped for another cancelable which causes the previous underlying
 * cancelable to be canceled.
 *
 * Example:
 * {{{
 *   val s = SerialCancelable()
 *   s := c1 // sets the underlying cancelable to c1
 *   s := c2 // cancels c1 and swaps the underlying cancelable to c2
 *
 *   s.cancel() // also cancels c2
 *
 *   s() = c3 // also cancels c3, because s is already canceled
 * }}}
 *
 * Also see [[MultiAssignmentCancelable]], which is similar, but doesn't cancel
 * the old cancelable upon assignment.
 */
final class SerialCancelable private () extends BooleanCancelable {
  import SerialCancelable.State
  import SerialCancelable.State._

  private[this] val state = Atomic(Active(Cancelable()) : State)

  def isCanceled: Boolean = state.get match {
    case Cancelled => true
    case _ => false
  }

  @tailrec
  def cancel(): Boolean = state.get match {
    case Cancelled => false
    case current @ Active(s) =>
      if (state.compareAndSet(current, Cancelled)) {
        s.cancel()
        true
      }
      else
        cancel()
  }

  /**
   * Swaps the underlying cancelable reference with `s`.
   *
   * In case this `SerialCancelable` is already canceled,
   * then the reference `value` will also be canceled on assignment.
   */
  @tailrec
  def update(value: Cancelable): Unit = state.get match {
    case Cancelled => value.cancel()
    case current @ Active(s) =>
      if (!state.compareAndSet(current, Active(value)))
        update(value)
      else
        s.cancel()
  }

  /**
   * Alias for `update(value)`
   */
  def `:=`(value: Cancelable): Unit =
    update(value)
}

object SerialCancelable {
  def apply(): SerialCancelable =
    new SerialCancelable()

  def apply(s: Cancelable): SerialCancelable = {
    val ms = new SerialCancelable()
    ms() = s
    ms
  }

  private sealed trait State

  private object State {
    case class Active(s: Cancelable) extends State
    case object Cancelled extends State
  }
}