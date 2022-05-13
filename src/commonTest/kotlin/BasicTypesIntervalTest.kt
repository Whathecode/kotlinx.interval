package com.github.whathecode.kotlinx.interval


class ByteIntervalTest : IntervalTest<Byte>( 0, 5, 10 )
{
    override fun createInterval( start: Byte, isStartIncluded: Boolean, end: Byte, isEndIncluded: Boolean ) =
        ByteInterval( start, isStartIncluded, end, isEndIncluded )
}

class ShortIntervalTest : IntervalTest<Short>( 0, 5, 10 )
{
    override fun createInterval( start: Short, isStartIncluded: Boolean, end: Short, isEndIncluded: Boolean ) =
        ShortInterval( start, isStartIncluded, end, isEndIncluded )
}

class IntIntervalTest : IntervalTest<Int>( 0, 5, 10 )
{
    override fun createInterval( start: Int, isStartIncluded: Boolean, end: Int, isEndIncluded: Boolean ) =
        IntInterval( start, isStartIncluded, end, isEndIncluded )
}

class LongIntervalTest : IntervalTest<Long>( 0, 5, 10 )
{
    override fun createInterval( start: Long, isStartIncluded: Boolean, end: Long, isEndIncluded: Boolean ) =
        LongInterval( start, isStartIncluded, end, isEndIncluded )
}

class FloatIntervalTest : IntervalTest<Float>( 0f, 5f, 10f )
{
    override fun createInterval( start: Float, isStartIncluded: Boolean, end: Float, isEndIncluded: Boolean ) =
        FloatInterval( start, isStartIncluded, end, isEndIncluded )
}

class DoubleIntervalTest : IntervalTest<Double>( 0.0, 5.0, 10.0 )
{
    override fun createInterval( start: Double, isStartIncluded: Boolean, end: Double, isEndIncluded: Boolean ) =
        DoubleInterval( start, isStartIncluded, end, isEndIncluded )
}

class UByteIntervalTest : IntervalTest<UByte>( 0.toUByte(), 5.toUByte(), 10.toUByte() )
{
    override fun createInterval( start: UByte, isStartIncluded: Boolean, end: UByte, isEndIncluded: Boolean ) =
        UByteInterval( start, isStartIncluded, end, isEndIncluded )
}

class UShortIntervalTest : IntervalTest<UShort>( 0.toUShort(), 5.toUShort(), 10.toUShort() )
{
    override fun createInterval( start: UShort, isStartIncluded: Boolean, end: UShort, isEndIncluded: Boolean ) =
        UShortInterval( start, isStartIncluded, end, isEndIncluded )
}

class UIntIntervalTest : IntervalTest<UInt>( 0.toUInt(), 5.toUInt(), 10.toUInt() )
{
    override fun createInterval( start: UInt, isStartIncluded: Boolean, end: UInt, isEndIncluded: Boolean ) =
        UIntInterval( start, isStartIncluded, end, isEndIncluded )
}

class ULongIntervalTest : IntervalTest<ULong>( 0.toULong(), 5.toULong(), 10.toULong() )
{
    override fun createInterval( start: ULong, isStartIncluded: Boolean, end: ULong, isEndIncluded: Boolean ) =
        ULongInterval( start, isStartIncluded, end, isEndIncluded )
}

class CharIntervalTest : IntervalTest<Char>( 'a', 'b', 'c' )
{
    override fun createInterval( start: Char, isStartIncluded: Boolean, end: Char, isEndIncluded: Boolean ) =
        CharInterval( start, isStartIncluded, end, isEndIncluded )
}
