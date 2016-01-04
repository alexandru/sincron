package scalax.concurrent.cancelables

import scalax.concurrent.atomic.AtomicAny
import scala.annotation.tailrec
import scalax.concurrent.Cancelable


/**
 * Represents a [[scalax.concurrent.Cancelable]] that can be assigned only
 * once to another cancelable reference.
 *
 * Similar to [[scalax.concurrent.cancelables.MultiAssignmentCancelable]],
 * except that in case of multi-assignment, it throws a
 * `java.lang.IllegalStateException`.
 *
 * If the assignment happens after this cancelable has been canceled, then on
 * assignment the reference will get canceled too.
 *
 * Useful in case you need a forward reference.
 */
final class SingleAssignmentCancelable private ()
  extends AssignableCancelable {

  import State._

  def isCanceled: Boolean = state.get match {
    case IsEmptyCanceled | IsCanceled =>
      true
    case _ =>
      false
  }

  /** Sets the underlying cancelable reference with `s`.
    *
    * In case this `SingleAssignmentCancelable` is already canceled,
    * then the reference `value` will also be canceled on assignment.
    *
    * Throws `IllegalStateException` in case this cancelable has already
    * been assigned.
    *
    * @return `this`
    */
  @throws(classOf[IllegalStateException])
  @tailrec
  override def `:=`(value: Cancelable): this.type =
    state.get match {
      case Empty =>
        if (!state.compareAndSet(Empty, IsNotCanceled(value)))
          :=(value)
        else
          this
      case IsEmptyCanceled =>
        if (!state.compareAndSet(IsEmptyCanceled, IsCanceled))
          :=(value)
        else {
          value.cancel()
          this
        }
      case IsCanceled | IsNotCanceled(_) =>
        throw new IllegalStateException("Cannot assign to SingleAssignmentCancelable, as it was already assigned once")
    }

  @tailrec
  def cancel(): Boolean = state.get match {
    case Empty =>
      if (!state.compareAndSet(Empty, IsEmptyCanceled))
        cancel()
      else
        true
    case old @ IsNotCanceled(s) =>
      if (!state.compareAndSet(old, IsCanceled))
        cancel()
      else {
        s.cancel()
        true
      }
    case IsEmptyCanceled | IsCanceled =>
      false
  }

  private[this] val state = AtomicAny(Empty : State)

  private[this] sealed trait State
  private[this] object State {
    case object Empty extends State
    case class IsNotCanceled(s: Cancelable) extends State
    case object IsCanceled extends State
    case object IsEmptyCanceled extends State
  }
}

object SingleAssignmentCancelable {
  def  apply(): SingleAssignmentCancelable =
    new SingleAssignmentCancelable()
}