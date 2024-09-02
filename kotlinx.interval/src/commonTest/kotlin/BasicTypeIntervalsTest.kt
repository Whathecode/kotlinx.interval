package io.github.whathecode.kotlinx.interval

import io.github.whathecode.kotlinx.interval.test.IntervalTest


object ByteIntervalTest : IntervalTest<Byte, UByte>( 0, 5, 10, 5.toUByte(), ByteInterval.Operations )
object ShortIntervalTest : IntervalTest<Short, UShort>( 0, 5, 10, 5.toUShort(), ShortInterval.Operations )
object IntIntervalTest : IntervalTest<Int, UInt>( 0, 5, 10, 5.toUInt(), IntInterval.Operations )
object LongIntervalTest : IntervalTest<Long, ULong>( 0, 5, 10, 5.toULong(), LongInterval.Operations )
object FloatIntervalTest : IntervalTest<Float, Double>( 0f, 5f, 10f, 5.0, FloatInterval.Operations )
object DoubleIntervalTest : IntervalTest<Double, Double>( 0.0, 5.0, 10.0, 5.0, DoubleInterval.Operations )
object UByteIntervalTest :
    IntervalTest<UByte, UByte>( 0.toUByte(), 5.toUByte(), 10.toUByte(), 5.toUByte(), UByteInterval.Operations )
object UShortIntervalTest :
    IntervalTest<UShort, UShort>( 0.toUShort(), 5.toUShort(), 10.toUShort(), 5.toUShort(), UShortInterval.Operations )
object UIntIntervalTest :
    IntervalTest<UInt, UInt>( 0.toUInt(), 5.toUInt(), 10.toUInt(), 5.toUInt(), UIntInterval.Operations )
object ULongIntervalTest :
    IntervalTest<ULong, ULong>( 0.toULong(), 5.toULong(), 10.toULong(), 5.toULong(), ULongInterval.Operations )
object CharIntervalTest : IntervalTest<Char, UShort>( 'a', 'c', 'e', 2.toUShort(), CharInterval.Operations )
