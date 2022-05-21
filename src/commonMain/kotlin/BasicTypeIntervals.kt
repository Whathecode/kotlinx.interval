package com.github.whathecode.kotlinx.interval

import kotlin.math.absoluteValue


/**
 * An [Interval] representing the set of all [Byte] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class ByteInterval( start: Byte, isStartIncluded: Boolean, end: Byte, isEndIncluded: Boolean )
    : Interval<Byte, UByte>( start, isStartIncluded, end, isEndIncluded, ByteOperations, UByteOperations )
{
    override fun getDistanceTo( value: Byte ): UByte =
        if ( value < 0 ) (0 - value).toUByte()
        else value.toUByte()
}

/**
 * An [Interval] representing the set of all [Short] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class ShortInterval( start: Short, isStartIncluded: Boolean, end: Short, isEndIncluded: Boolean )
    : Interval<Short, UShort>( start, isStartIncluded, end, isEndIncluded, ShortOperations, UShortOperations )
{
    override fun getDistanceTo( value: Short ): UShort =
        if ( value < 0 ) (0 - value).toUShort()
        else value.toUShort()
}

/**
 * An [Interval] representing the set of all [Int] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class IntInterval( start: Int, isStartIncluded: Boolean, end: Int, isEndIncluded: Boolean )
    : Interval<Int, UInt>( start, isStartIncluded, end, isEndIncluded, IntOperations, UIntOperations )
{
    override fun getDistanceTo( value: Int ): UInt =
        if ( value < 0 ) (0 - value).toUInt()
        else value.toUInt()
}

/**
 * An [Interval] representing the set of all [Long] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class LongInterval( start: Long, isStartIncluded: Boolean, end: Long, isEndIncluded: Boolean )
    : Interval<Long, ULong>( start, isStartIncluded, end, isEndIncluded, LongOperations, ULongOperations )
{
    override fun getDistanceTo( value: Long ): ULong =
        if ( value < 0 ) (0 - value).toULong()
        else value.toULong()
}

/**
 * An [Interval] representing the set of all [Float] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class FloatInterval( start: Float, isStartIncluded: Boolean, end: Float, isEndIncluded: Boolean )
    : Interval<Float, Double>( start, isStartIncluded, end, isEndIncluded, FloatOperations, DoubleOperations )
{
    override fun getDistanceTo( value: Float ): Double = value.absoluteValue.toDouble()
}

/**
 * An [Interval] representing the set of all [Double] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 *
 * TODO: Double can't safely represent all sizes for Double intervals.
 */
class DoubleInterval( start: Double, isStartIncluded: Boolean, end: Double, isEndIncluded: Boolean )
    : Interval<Double, Double>( start, isStartIncluded, end, isEndIncluded, DoubleOperations, DoubleOperations )
{
    override fun getDistanceTo( value: Double ): Double = value.absoluteValue
}

/**
 * An [Interval] representing the set of all [UByte] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class UByteInterval( start: UByte, isStartIncluded: Boolean, end: UByte, isEndIncluded: Boolean )
    : Interval<UByte, UByte>( start, isStartIncluded, end, isEndIncluded, UByteOperations, UByteOperations )
{
    override fun getDistanceTo( value: UByte ): UByte = value
}

/**
 * An [Interval] representing the set of all [UShort] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class UShortInterval( start: UShort, isStartIncluded: Boolean, end: UShort, isEndIncluded: Boolean )
    : Interval<UShort, UShort>( start, isStartIncluded, end, isEndIncluded, UShortOperations, UShortOperations )
{
    override fun getDistanceTo( value: UShort ): UShort = value
}

/**
 * An [Interval] representing the set of all [UInt] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class UIntInterval( start: UInt, isStartIncluded: Boolean, end: UInt, isEndIncluded: Boolean )
    : Interval<UInt, UInt>( start, isStartIncluded, end, isEndIncluded, UIntOperations, UIntOperations )
{
    override fun getDistanceTo( value: UInt ): UInt = value
}

/**
 * An [Interval] representing the set of all [ULong] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class ULongInterval( start: ULong, isStartIncluded: Boolean, end: ULong, isEndIncluded: Boolean )
    : Interval<ULong, ULong>( start, isStartIncluded, end, isEndIncluded, ULongOperations, ULongOperations )
{
    override fun getDistanceTo( value: ULong ): ULong = value
}

/**
 * An [Interval] representing the set of all [Char] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class CharInterval( start: Char, isStartIncluded: Boolean, end: Char, isEndIncluded: Boolean )
    : Interval<Char, UShort>( start, isStartIncluded, end, isEndIncluded, CharOperations, UShortOperations )
{
    override fun getDistanceTo( value: Char ): UShort = value.code.toUShort()
}
