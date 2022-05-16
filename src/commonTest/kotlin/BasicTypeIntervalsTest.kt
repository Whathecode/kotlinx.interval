package com.github.whathecode.kotlinx.interval


object ByteIntervalTest : IntervalTest<Byte>
    by createIntervalTest( 0, 5, 10, 5, 0 )
object ShortIntervalTest : IntervalTest<Short>
    by createIntervalTest( 0, 5, 10, 5, 0 )
object IntIntervalTest : IntervalTest<Int>
    by createIntervalTest( 0, 5, 10, 5, 0 )
object LongIntervalTest : IntervalTest<Long>
    by createIntervalTest( 0, 5, 10, 5, 0 )
object FloatIntervalTest : IntervalTest<Float>
    by createIntervalTest( 0f, 5f, 10f, 5f, 0f )
object DoubleIntervalTest : IntervalTest<Double>
    by createIntervalTest( 0.0, 5.0, 10.0, 5.0, 0.0 )
object UByteIntervalTest : IntervalTest<UByte>
    by createIntervalTest( 0.toUByte(), 5.toUByte(), 10.toUByte(), 5.toUByte(), 0.toUByte() )
object UShortIntervalTest : IntervalTest<UShort>
    by createIntervalTest( 0.toUShort(), 5.toUShort(), 10.toUShort(), 5.toUShort(), 0.toUShort() )
object UIntIntervalTest : IntervalTest<UInt>
    by createIntervalTest( 0.toUInt(), 5.toUInt(), 10.toUInt(), 5.toUInt(), 0.toUInt() )
object ULongIntervalTest : IntervalTest<ULong>
    by createIntervalTest( 0.toULong(), 5.toULong(), 10.toULong(), 5.toULong(), 0.toULong() )
object CharIntervalTest : IntervalTest<Char>
    by createIntervalTest( 'a', 'b', 'c', 1.toChar(), 0.toChar() )
