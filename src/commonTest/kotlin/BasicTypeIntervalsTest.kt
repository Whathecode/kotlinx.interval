package com.github.whathecode.kotlinx.interval


object ByteIntervalTest : IntervalTest<Byte, UByte>
    by createIntervalTest( 0, 5, 10, 5.toUByte(), { s, si, e, ei -> ByteInterval( s, si, e, ei ) } )
object ShortIntervalTest : IntervalTest<Short, UShort>
    by createIntervalTest( 0, 5, 10, 5.toUShort(), { s, si, e, ei -> ShortInterval( s, si, e, ei ) } )
object IntIntervalTest : IntervalTest<Int, UInt>
    by createIntervalTest( 0, 5, 10, 5.toUInt(), { s, si, e, ei -> IntInterval( s, si, e, ei ) } )
object LongIntervalTest : IntervalTest<Long, ULong>
    by createIntervalTest( 0, 5, 10, 5.toULong(), { s, si, e, ei -> LongInterval( s, si, e, ei ) } )
object FloatIntervalTest : IntervalTest<Float, Double>
    by createIntervalTest( 0f, 5f, 10f, 5.0, { s, si, e, ei -> FloatInterval( s, si, e, ei ) } )
object DoubleIntervalTest : IntervalTest<Double, Double>
    by createIntervalTest( 0.0, 5.0, 10.0, 5.0, { s, si, e, ei -> DoubleInterval( s, si, e, ei ) } )
object UByteIntervalTest : IntervalTest<UByte, UByte>
    by createIntervalTest(
        0.toUByte(), 5.toUByte(), 10.toUByte(), 5.toUByte(),
        { s, si, e, ei -> UByteInterval( s, si, e, ei ) }
    )
object UShortIntervalTest : IntervalTest<UShort, UShort>
    by createIntervalTest(
        0.toUShort(), 5.toUShort(), 10.toUShort(), 5.toUShort(),
        { s, si, e, ei -> UShortInterval( s, si, e, ei ) }
    )
object UIntIntervalTest : IntervalTest<UInt, UInt>
    by createIntervalTest(
        0.toUInt(), 5.toUInt(), 10.toUInt(), 5.toUInt(),
        { s, si, e, ei -> UIntInterval( s, si, e, ei ) }
    )
object ULongIntervalTest : IntervalTest<ULong, ULong>
    by createIntervalTest(
        0.toULong(), 5.toULong(), 10.toULong(), 5.toULong(),
        { s, si, e, ei -> ULongInterval( s, si, e, ei ) }
    )
object CharIntervalTest : IntervalTest<Char, UShort>
    by createIntervalTest( 'a', 'b', 'c', 1.toUShort(), { s, si, e, ei -> CharInterval( s, si, e, ei ) } )
