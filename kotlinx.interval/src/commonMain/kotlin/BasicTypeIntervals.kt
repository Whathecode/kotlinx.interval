package io.github.whathecode.kotlinx.interval

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
        internal val Operations = createIntervalTypeOperations<Byte, UByte>(
            getDistanceTo =
            {
                if ( it < 0 ) (0 - it).toUByte()
                else it.toUByte()
            },
            unsafeValueAt = { it.toByte() }
        )
    }
}

/**
 * Create a [ByteInterval] representing the set of all [Byte] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: Byte, end: Byte, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    ByteInterval( start, isStartIncluded, end, isEndIncluded )


/**
 * An [Interval] representing the set of all [Short] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class ShortInterval( start: Short, isStartIncluded: Boolean, end: Short, isEndIncluded: Boolean )
    : Interval<Short, UShort>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<Short, UShort>(
            getDistanceTo =
            {
                if ( it < 0 ) (0 - it).toUShort()
                else it.toUShort()
            },
            unsafeValueAt = { it.toShort() }
        )
    }
}

/**
 * Create a [ShortInterval] representing the set of all [Short] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: Short, end: Short, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    ShortInterval( start, isStartIncluded, end, isEndIncluded )


/**
 * An [Interval] representing the set of all [Int] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class IntInterval( start: Int, isStartIncluded: Boolean, end: Int, isEndIncluded: Boolean )
    : Interval<Int, UInt>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<Int, UInt>(
            getDistanceTo =
            {
                if ( it < 0 ) (0 - it).toUInt()
                else it.toUInt()
            },
            unsafeValueAt = { it.toInt() }
        )
    }
}

/**
 * Create a [IntInterval] representing the set of all [Int] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: Int, end: Int, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    IntInterval( start, isStartIncluded, end, isEndIncluded )


/**
 * An [Interval] representing the set of all [Long] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class LongInterval( start: Long, isStartIncluded: Boolean, end: Long, isEndIncluded: Boolean )
    : Interval<Long, ULong>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<Long, ULong>(
            getDistanceTo =
            {
                if ( it < 0 ) (0 - it).toULong()
                else it.toULong()
            },
            unsafeValueAt = { it.toLong() }
        )
    }
}

/**
 * Create a [LongInterval] representing the set of all [Long] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: Long, end: Long, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    LongInterval( start, isStartIncluded, end, isEndIncluded )


/**
 * An [Interval] representing the set of all [Float] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class FloatInterval( start: Float, isStartIncluded: Boolean, end: Float, isEndIncluded: Boolean )
    : Interval<Float, Double>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<Float, Double>(
            getDistanceTo = { it.absoluteValue.toDouble() },
            unsafeValueAt = { it.absoluteValue.toFloat() }
        )
    }
}

/**
 * Create a [FloatInterval] representing the set of all [Float] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: Float, end: Float, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    FloatInterval( start, isStartIncluded, end, isEndIncluded )


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
        internal val Operations = createIntervalTypeOperations<Double, Double>(
            getDistanceTo = { it.absoluteValue },
            unsafeValueAt = { it.absoluteValue }
        )
    }
}

/**
 * Create a [DoubleInterval] representing the set of all [Double] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: Double, end: Double, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    DoubleInterval( start, isStartIncluded, end, isEndIncluded )


/**
 * An [Interval] representing the set of all [UByte] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class UByteInterval( start: UByte, isStartIncluded: Boolean, end: UByte, isEndIncluded: Boolean )
    : Interval<UByte, UByte>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<UByte, UByte>(
            getDistanceTo = { it },
            unsafeValueAt = { it },
        )
    }
}

/**
 * Create a [UByteInterval] representing the set of all [UByte] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: UByte, end: UByte, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    UByteInterval( start, isStartIncluded, end, isEndIncluded )


/**
 * An [Interval] representing the set of all [UShort] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class UShortInterval( start: UShort, isStartIncluded: Boolean, end: UShort, isEndIncluded: Boolean )
    : Interval<UShort, UShort>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<UShort, UShort>(
            getDistanceTo = { it },
            unsafeValueAt = { it }
        )
    }
}

/**
 * Create a [UShortInterval] representing the set of all [UShort] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: UShort, end: UShort, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    UShortInterval( start, isStartIncluded, end, isEndIncluded )


/**
 * An [Interval] representing the set of all [UInt] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class UIntInterval( start: UInt, isStartIncluded: Boolean, end: UInt, isEndIncluded: Boolean )
    : Interval<UInt, UInt>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<UInt, UInt>(
            getDistanceTo = { it },
            unsafeValueAt = { it }
        )
    }
}

/**
 * Create a [UIntInterval] representing the set of all [UInt] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: UInt, end: UInt, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    UIntInterval( start, isStartIncluded, end, isEndIncluded )


/**
 * An [Interval] representing the set of all [ULong] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class ULongInterval( start: ULong, isStartIncluded: Boolean, end: ULong, isEndIncluded: Boolean )
    : Interval<ULong, ULong>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<ULong, ULong>(
            getDistanceTo = { it },
            unsafeValueAt = { it }
        )
    }
}

/**
 * Create a [ULongInterval] representing the set of all [ULong] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: ULong, end: ULong, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    ULongInterval( start, isStartIncluded, end, isEndIncluded )


/**
 * An [Interval] representing the set of all [Char] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class CharInterval( start: Char, isStartIncluded: Boolean, end: Char, isEndIncluded: Boolean )
    : Interval<Char, UShort>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = createIntervalTypeOperations<Char, UShort>(
            getDistanceTo = { it.code.toUShort() },
            unsafeValueAt = { Char( it ) }
        )
    }
}

/**
 * Create a [CharInterval] representing the set of all [Char] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: Char, end: Char, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    CharInterval( start, isStartIncluded, end, isEndIncluded )
