package scalax.concurrent.atomic.padded

import scalax.concurrent.atomic

trait AtomicBuilder[T, R <: atomic.Atomic[T]] extends atomic.AtomicBuilder[T, R]

object AtomicBuilder extends Implicits.Level3

private[padded] object Implicits {
  trait Level1 {
    implicit def AtomicRefBuilder[T]: AtomicBuilder[T, AtomicAny[T]] =
      new AtomicBuilder[T, AtomicAny[T]] {
        def buildInstance(initialValue: T) =
          AtomicAny(initialValue)
      }
  }

  trait Level2 extends Level1 {
    implicit def AtomicNumberBuilder[T : Numeric]: AtomicBuilder[T, AtomicNumberAny[T]] =
      new AtomicBuilder[T, AtomicNumberAny[T]] {
        def buildInstance(initialValue: T) =
          AtomicNumberAny(initialValue)
      }
  }

  trait Level3 extends Level2 {
    implicit val AtomicIntBuilder =
      new AtomicBuilder[Int, AtomicInt] {
        def buildInstance(initialValue: Int) =
          AtomicInt(initialValue)
      }

    implicit val AtomicLongBuilder =
      new AtomicBuilder[Long, AtomicLong] {
        def buildInstance(initialValue: Long) =
          AtomicLong(initialValue)
      }

    implicit val AtomicBooleanBuilder =
      new AtomicBuilder[Boolean, AtomicBoolean] {
        def buildInstance(initialValue: Boolean) =
          AtomicBoolean(initialValue)
      }

    implicit val AtomicByteBuilder =
      new AtomicBuilder[Byte, AtomicByte] {
        def buildInstance(initialValue: Byte): AtomicByte =
          AtomicByte(initialValue)
      }

    implicit val AtomicCharBuilder =
      new AtomicBuilder[Char, AtomicChar] {
        def buildInstance(initialValue: Char): AtomicChar =
          AtomicChar(initialValue)
      }

    implicit val AtomicShortBuilder =
      new AtomicBuilder[Short, AtomicShort] {
        def buildInstance(initialValue: Short): AtomicShort =
          AtomicShort(initialValue)
      }

    implicit val AtomicFloatBuilder =
      new AtomicBuilder[Float, AtomicFloat] {
        def buildInstance(initialValue: Float): AtomicFloat =
          AtomicFloat(initialValue)
      }

    implicit val AtomicDoubleBuilder =
      new AtomicBuilder[Double, AtomicDouble] {
        def buildInstance(initialValue: Double): AtomicDouble =
          AtomicDouble(initialValue)
      }
  }
}

