package scalax.concurrent.atomic.padded

import scalax.concurrent.atomic.ConcurrentAtomicNumberSuite

object ConcurrentAtomicNumberDoubleSuite extends ConcurrentAtomicNumberSuite[Double, AtomicDouble](
  "AtomicDouble", Atomic.builderFor(0.0), 17.23, Some(Double.NaN), Double.MaxValue, Double.MinValue)

object ConcurrentAtomicNumberFloatSuite extends ConcurrentAtomicNumberSuite[Float, AtomicFloat](
  "AtomicFloat", Atomic.builderFor(0.0f), 17.23f, Some(Float.NaN), Float.MaxValue, Float.MinValue)

object ConcurrentAtomicNumberLongSuite extends ConcurrentAtomicNumberSuite[Long, AtomicLong](
  "AtomicLong", Atomic.builderFor(0L), -782L, None, Long.MaxValue, Long.MinValue)

object ConcurrentAtomicNumberIntSuite extends ConcurrentAtomicNumberSuite[Int, AtomicInt](
  "AtomicInt", Atomic.builderFor(0), 782, None, Int.MaxValue, Int.MinValue)

object ConcurrentAtomicNumberShortSuite extends ConcurrentAtomicNumberSuite[Short, AtomicShort](
  "AtomicShort", Atomic.builderFor(0.toShort), 782.toShort, None, Short.MaxValue, Short.MinValue)

object ConcurrentAtomicNumberByteSuite extends ConcurrentAtomicNumberSuite[Byte, AtomicByte](
  "AtomicByte", Atomic.builderFor(0.toByte), 782.toByte, None, Byte.MaxValue, Byte.MinValue)

object ConcurrentAtomicNumberCharSuite extends ConcurrentAtomicNumberSuite[Char, AtomicChar](
  "AtomicChar", Atomic.builderFor(0.toChar), 782.toChar, None, Char.MaxValue, Char.MinValue)

object ConcurrentAtomicNumberNumberAnySuite extends ConcurrentAtomicNumberSuite[BigInt, AtomicNumberAny[BigInt]](
  "AtomicNumberAny", Atomic.builderFor(BigInt(0)), BigInt(Int.MaxValue), None, BigInt(Long.MaxValue), BigInt(Long.MinValue))
