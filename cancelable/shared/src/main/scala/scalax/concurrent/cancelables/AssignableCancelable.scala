package scalax.concurrent.cancelables

import scalax.concurrent.Cancelable

/** Represents a class of cancelables that can hold
  * an internal reference to another cancelable (and thus
  * has to support the assignment operator).
  *
  * Examples are the [[MultiAssignmentCancelable]] and the
  * [[SingleAssignmentCancelable]].
  *
  * NOTE that on assignment, if this cancelable is already
  * canceled, then no assignment should happen and the update
  * reference should be canceled as well.
  */
trait AssignableCancelable extends BooleanCancelable {
  /** Updates the internal reference of this assignable cancelable
    * to the given value.
    *
    * If this cancelable is already canceled, then `value` is
    * going to be canceled on assigned as well.
    *
    * @return `this`
    */
  def `:=`(value: Cancelable): this.type
}
