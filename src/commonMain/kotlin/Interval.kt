package com.github.whathecode.kotlinx.interval


/**
 * Represents a set of all [T] values lying between a provided [start] and [end] value.
 * [TSize] is the type used to represent the distance between [T] values.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 *
 * @throws IllegalArgumentException if an open or half-open interval with the same start and end value is specified.
 */
open class Interval<T : Comparable<T>, TSize : Comparable<TSize>>(
    val start: T,
    val isStartIncluded: Boolean,
    val end: T,
    val isEndIncluded: Boolean,
    /**
     * Provide access to the predefined set of operators of [T] and [TSize] and conversions between them.
     */
    private val operations: IntervalTypeOperations<T, TSize>,
)
{
    init
    {
        if ( !isStartIncluded || !isEndIncluded )
        {
            require( start != end ) { "Open or half-open intervals should have differing start and end value." }
        }
    }

    private val valueOperations = operations.valueOperations
    private val sizeOperations = operations.sizeOperations


    /**
     * Determines whether both [start] and [end] are included in the interval.
     */
    inline val isClosedInterval: Boolean get() = isStartIncluded && isEndIncluded

    /**
     * Determines whether both [start] and [end] are excluded from the interval.
     */
    inline val isOpenInterval: Boolean get() = !isStartIncluded && !isEndIncluded

    /**
     * Determines whether the [start] of the interval is greater than the [end].
     */
    inline val isReversed: Boolean get() =  start > end

    /**
     * The absolute difference between [start] and [end].
     */
    val size: TSize get()
    {
        val zero = valueOperations.additiveIdentity
        val startDistance = operations.getDistanceTo( start )
        val endDistance = operations.getDistanceTo( end )
        val valuesHaveOppositeSign = start <= zero != end <= zero

        return if ( valuesHaveOppositeSign )
        {
            sizeOperations.unsafeAdd( startDistance, endDistance )
        }
        else
        {
            if ( startDistance < endDistance ) sizeOperations.unsafeSubtract( endDistance, startDistance )
            else sizeOperations.unsafeSubtract( startDistance, endDistance )
        }
    }

    /**
     * Determines whether this interval equals [other]'s constructor parameters exactly,
     * i.e., not whether they represent the same set of [T] values, such as matching inverse intervals.
     */
    override fun equals( other: Any? ): Boolean
    {
        if ( other !is Interval<*, *> ) return false

        return this.start == other.start
            && this.end == other.end
            && this.isStartIncluded == other.isStartIncluded
            && this.isEndIncluded == other.isEndIncluded
    }

    override fun hashCode(): Int
    {
        var result = start.hashCode()
        result = 31 * result + isStartIncluded.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + isEndIncluded.hashCode()
        return result
    }
}
