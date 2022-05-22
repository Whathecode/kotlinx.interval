package com.github.whathecode.kotlinx.interval

import kotlin.math.absoluteValue


/**
 * An [Interval] representing the set of all [Byte] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class ByteInterval( start: Byte, isStartIncluded: Boolean, end: Byte, isEndIncluded: Boolean )
    : Interval<Byte, UByte>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<Byte, UByte>()
            {
                if ( it < 0 ) (0 - it).toUByte()
                else it.toUByte()
            }
    }
}

/**
 * An [Interval] representing the set of all [Short] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class ShortInterval( start: Short, isStartIncluded: Boolean, end: Short, isEndIncluded: Boolean )
    : Interval<Short, UShort>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<Short, UShort>()
            {
                if ( it < 0 ) (0 - it).toUShort()
                else it.toUShort()
            }
    }
}

/**
 * An [Interval] representing the set of all [Int] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class IntInterval( start: Int, isStartIncluded: Boolean, end: Int, isEndIncluded: Boolean )
    : Interval<Int, UInt>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<Int, UInt>()
            {
                if ( it < 0 ) (0 - it).toUInt()
                else it.toUInt()
            }
    }
}

/**
 * An [Interval] representing the set of all [Long] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class LongInterval( start: Long, isStartIncluded: Boolean, end: Long, isEndIncluded: Boolean )
    : Interval<Long, ULong>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<Long, ULong>()
            {
                if ( it < 0 ) (0 - it).toULong()
                else it.toULong()
            }
    }
}

/**
 * An [Interval] representing the set of all [Float] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class FloatInterval( start: Float, isStartIncluded: Boolean, end: Float, isEndIncluded: Boolean )
    : Interval<Float, Double>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<Float, Double> { it.absoluteValue.toDouble() }
    }
}

/**
 * An [Interval] representing the set of all [Double] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 *
 * The [size] of [Double] intervals which exceed [Double.MAX_VALUE] will be [Double.POSITIVE_INFINITY].
 */
class DoubleInterval( start: Double, isStartIncluded: Boolean, end: Double, isEndIncluded: Boolean )
    : Interval<Double, Double>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<Double, Double> { it.absoluteValue }
    }
}

/**
 * An [Interval] representing the set of all [UByte] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class UByteInterval( start: UByte, isStartIncluded: Boolean, end: UByte, isEndIncluded: Boolean )
    : Interval<UByte, UByte>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<UByte, UByte> { it }
    }
}

/**
 * An [Interval] representing the set of all [UShort] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class UShortInterval( start: UShort, isStartIncluded: Boolean, end: UShort, isEndIncluded: Boolean )
    : Interval<UShort, UShort>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<UShort, UShort> { it }
    }
}

/**
 * An [Interval] representing the set of all [UInt] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class UIntInterval( start: UInt, isStartIncluded: Boolean, end: UInt, isEndIncluded: Boolean )
    : Interval<UInt, UInt>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<UInt, UInt> { it }
    }
}

/**
 * An [Interval] representing the set of all [ULong] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class ULongInterval( start: ULong, isStartIncluded: Boolean, end: ULong, isEndIncluded: Boolean )
    : Interval<ULong, ULong>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<ULong, ULong> { it }
    }
}

/**
 * An [Interval] representing the set of all [Char] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class CharInterval( start: Char, isStartIncluded: Boolean, end: Char, isEndIncluded: Boolean )
    : Interval<Char, UShort>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<Char, UShort> { it.code.toUShort() }
    }
}
