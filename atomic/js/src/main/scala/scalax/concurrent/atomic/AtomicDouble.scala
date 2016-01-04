package scalax.concurrent.atomic

final class AtomicDouble private[atomic]
  (initialValue: Double) extends AtomicNumber[Double] {

  private[this] var ref = initialValue

  def getAndSet(update: Double): Double = {
    val current = ref
    ref = update
    current
  }

  def compareAndSet(expect: Double, update: Double): Boolean = {
    if (ref == expect) {
      ref = update
      true
    }
    else
      false
  }

  def set(update: Double): Unit = {
    ref = update
  }

  def get: Double = ref

  @inline
  def update(value: Double): Unit = set(value)

  @inline
  def `:=`(value: Double): Unit = set(value)

  @inline
  def lazySet(update: Double): Unit = set(update)

  def transformAndExtract[U](cb: (Double) => (U, Double)): U = {
    val (r, update) = cb(ref)
    ref = update
    r
  }

  def transformAndGet(cb: (Double) => Double): Double = {
    val update = cb(ref)
    ref = update
    update
  }

  def getAndTransform(cb: (Double) => Double): Double = {
    val current = ref
    ref = cb(ref)
    current
  }

  def transform(cb: (Double) => Double): Unit = {
    ref = cb(ref)
  }

  def getAndSubtract(v: Double): Double = {
    val c = ref
    ref = ref - v
    c
  }

  def subtractAndGet(v: Double): Double = {
    ref = ref - v
    ref
  }

  def subtract(v: Double): Unit = {
    ref = ref - v
  }

  def getAndAdd(v: Double): Double = {
    val c = ref
    ref = ref + v
    c
  }

  def getAndIncrement(v: Int = 1): Double = {
    val c = ref
    ref = ref + v
    c
  }

  def addAndGet(v: Double): Double = {
    ref = ref + v
    ref
  }

  def incrementAndGet(v: Int = 1): Double = {
    ref = ref + v
    ref
  }

  def add(v: Double): Unit = {
    ref = ref + v
  }

  def increment(v: Int = 1): Unit = {
    ref = ref + v
  }

  def countDownToZero(v: Double = 1): Double = {
    val current = get
    if (current != 0) {
      val decrement = if (current >= v) v else current
      ref = current - decrement
      decrement
    }
    else
      0
  }

  def decrement(v: Int = 1): Unit = increment(-v)
  def decrementAndGet(v: Int = 1): Double = incrementAndGet(-v)
  def getAndDecrement(v: Int = 1): Double = getAndIncrement(-v)
  def `+=`(v: Double): Unit = addAndGet(v)
  def `-=`(v: Double): Unit = subtractAndGet(v)
}

object AtomicDouble {
  def apply(initialValue: Double): AtomicDouble =
    new AtomicDouble(initialValue)
}
