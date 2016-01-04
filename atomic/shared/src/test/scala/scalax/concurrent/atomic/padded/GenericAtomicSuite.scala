package scalax.concurrent.atomic.padded

import scalax.concurrent.atomic.GenericAtomicSuite

object GenericAtomicAnySuite extends GenericAtomicSuite[String, AtomicAny[String]](
  "AtomicAny", Atomic.builderFor(""), x => x.toString, x => x.toInt)

object GenericAtomicBooleanSuite extends GenericAtomicSuite[Boolean, AtomicBoolean](
  "AtomicBoolean", Atomic.builderFor(true), x => if (x == 1) true else false, x => if (x) 1 else 0)

object GenericAtomicNumberAnySuite extends GenericAtomicSuite[Long, AtomicNumberAny[Long]](
  "AtomicNumberAny", AtomicBuilder.AtomicNumberBuilder[Long], x => x.toLong, x => x.toInt)

object GenericAtomicFloatSuite extends GenericAtomicSuite[Float, AtomicFloat](
  "AtomicFloat", Atomic.builderFor(0.0f), x => x.toFloat, x => x.toInt)

object GenericAtomicDoubleSuite extends GenericAtomicSuite[Double, AtomicDouble](
  "AtomicDouble", Atomic.builderFor(0.toDouble), x => x.toDouble, x => x.toInt)

object GenericAtomicShortSuite extends GenericAtomicSuite[Short, AtomicShort](
  "AtomicShort", Atomic.builderFor(0.toShort), x => x.toShort, x => x.toInt)

object GenericAtomicByteSuite extends GenericAtomicSuite[Byte, AtomicByte](
  "AtomicByte", Atomic.builderFor(0.toByte), x => x.toByte, x => x.toInt)

object GenericAtomicCharSuite extends GenericAtomicSuite[Char, AtomicChar](
  "AtomicChar", Atomic.builderFor(0.toChar), x => x.toChar, x => x.toInt)

object GenericAtomicIntSuite extends GenericAtomicSuite[Int, AtomicInt](
  "AtomicInt", Atomic.builderFor(0), x => x, x => x)

object GenericAtomicLongSuite extends GenericAtomicSuite[Long, AtomicLong](
  "AtomicLong", Atomic.builderFor(0.toLong), x => x.toLong, x => x.toInt)