package scalax.concurrent.atomic.padded

import scalax.concurrent.atomic.ConcurrentAtomicSuite

object ConcurrentAtomicAnySuite extends ConcurrentAtomicSuite[String, AtomicAny[String]](
  "AtomicAny", Atomic.builderFor(""), x => x.toString, x => x.toInt)

object ConcurrentAtomicBooleanSuite extends ConcurrentAtomicSuite[Boolean, AtomicBoolean](
  "AtomicBoolean", Atomic.builderFor(true), x => if (x == 1) true else false, x => if (x) 1 else 0)

object ConcurrentAtomicNumberAnySuite extends ConcurrentAtomicSuite[BigInt, AtomicNumberAny[BigInt]](
  "AtomicNumberAny", Atomic.builderFor(BigInt(0)), x => BigInt(x), x => x.toInt)

object ConcurrentAtomicFloatSuite extends ConcurrentAtomicSuite[Float, AtomicFloat](
  "AtomicFloat", Atomic.builderFor(0.0f), x => x.toFloat, x => x.toInt)

object ConcurrentAtomicDoubleSuite extends ConcurrentAtomicSuite[Double, AtomicDouble](
  "AtomicDouble", Atomic.builderFor(0.toDouble), x => x.toDouble, x => x.toInt)

object ConcurrentAtomicShortSuite extends ConcurrentAtomicSuite[Short, AtomicShort](
  "AtomicShort", Atomic.builderFor(0.toShort), x => x.toShort, x => x.toInt)

object ConcurrentAtomicByteSuite extends ConcurrentAtomicSuite[Byte, AtomicByte](
  "AtomicByte", Atomic.builderFor(0.toByte), x => x.toByte, x => x.toInt)

object ConcurrentAtomicCharSuite extends ConcurrentAtomicSuite[Char, AtomicChar](
  "AtomicChar", Atomic.builderFor(0.toChar), x => x.toChar, x => x.toInt)

object ConcurrentAtomicIntSuite extends ConcurrentAtomicSuite[Int, AtomicInt](
  "AtomicInt", Atomic.builderFor(0), x => x, x => x)

object ConcurrentAtomicLongSuite extends ConcurrentAtomicSuite[Long, AtomicLong](
  "AtomicLong", Atomic.builderFor(0.toLong), x => x.toLong, x => x.toInt)