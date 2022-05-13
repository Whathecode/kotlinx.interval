package com.github.whathecode.kotlinx.interval


/**
 * An [Interval] of type [Byte].
 */
class ByteInterval( start: Byte, isStartIncluded: Boolean, end: Byte, isEndIncluded: Boolean ) :
    Interval<Byte>( start, isStartIncluded, end, isEndIncluded )
{
    override fun subtract( a: Byte, b: Byte ): Byte = (a - b).toByte()
}

/**
 * An [Interval] of type [Short].
 */
class ShortInterval( start: Short, isStartIncluded: Boolean, end: Short, isEndIncluded: Boolean ) :
    Interval<Short>( start, isStartIncluded, end, isEndIncluded )
{
    override fun subtract( a: Short, b: Short ): Short = (a - b).toShort()
}

/**
 * An [Interval] of type [Int].
 */
class IntInterval( start: Int, isStartIncluded: Boolean, end: Int, isEndIncluded: Boolean ) :
    Interval<Int>( start, isStartIncluded, end, isEndIncluded )
{
    override fun subtract( a: Int, b: Int ): Int = a - b
}

/**
 * An [Interval] of type [Long].
 */
class LongInterval( start: Long, isStartIncluded: Boolean, end: Long, isEndIncluded: Boolean ) :
    Interval<Long>( start, isStartIncluded, end, isEndIncluded )
{
    override fun subtract( a: Long, b: Long ): Long = a - b
}

/**
 * An [Interval] of type [Float].
 */
class FloatInterval( start: Float, isStartIncluded: Boolean, end: Float, isEndIncluded: Boolean ) :
    Interval<Float>( start, isStartIncluded, end, isEndIncluded )
{
    override fun subtract( a: Float, b: Float ): Float = a - b
}

/**
 * An [Interval] of type [Double].
 */
class DoubleInterval( start: Double, isStartIncluded: Boolean, end: Double, isEndIncluded: Boolean ) :
    Interval<Double>( start, isStartIncluded, end, isEndIncluded )
{
    override fun subtract( a: Double, b: Double ): Double = a - b
}

/**
 * An [Interval] of type [UByte].
 */
class UByteInterval( start: UByte, isStartIncluded: Boolean, end: UByte, isEndIncluded: Boolean ) :
    Interval<UByte>( start, isStartIncluded, end, isEndIncluded )
{
    override fun subtract( a: UByte, b: UByte ): UByte = (a - b).toUByte()
}

/**
 * An [Interval] of type [UShort].
 */
class UShortInterval( start: UShort, isStartIncluded: Boolean, end: UShort, isEndIncluded: Boolean ) :
    Interval<UShort>( start, isStartIncluded, end, isEndIncluded )
{
    override fun subtract( a: UShort, b: UShort ): UShort = (a - b).toUShort()
}

/**
 * An [Interval] of type [UInt].
 */
class UIntInterval( start: UInt, isStartIncluded: Boolean, end: UInt, isEndIncluded: Boolean ) :
    Interval<UInt>( start, isStartIncluded, end, isEndIncluded )
{
    override fun subtract( a: UInt, b: UInt ): UInt = a - b
}

/**
 * An [Interval] of type [ULong].
 */
class ULongInterval( start: ULong, isStartIncluded: Boolean, end: ULong, isEndIncluded: Boolean ) :
    Interval<ULong>( start, isStartIncluded, end, isEndIncluded )
{
    override fun subtract( a: ULong, b: ULong ): ULong = a - b
}

/**
 * An [Interval] of type [Char].
 */
class CharInterval( start: Char, isStartIncluded: Boolean, end: Char, isEndIncluded: Boolean ) :
    Interval<Char>( start, isStartIncluded, end, isEndIncluded )
{
    override fun subtract( a: Char, b: Char ): Char = (a - b).toChar()
}
