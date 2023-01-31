package io.github.whathecode.kotlinx.interval


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
    inline val isReversed: Boolean get() = start > end

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
     * Checks whether [value] lies within this interval.
     */
    operator fun contains( value: T ): Boolean
    {
        class Endpoint( val value: T, val isIncluded: Boolean )

        val a = Endpoint( start, isStartIncluded )
        val b = Endpoint( end, isEndIncluded )
        val (smallest, greatest) = if ( isReversed ) b to a else a to b

        val smallestComp = value.compareTo( smallest.value )
        val greatestComp = value.compareTo( greatest.value )

        return ( smallestComp > 0 || (smallestComp == 0 && smallest.isIncluded) )
            && ( greatestComp < 0 || (greatestComp == 0 && greatest.isIncluded) )
    }

    /**
     * Return an [IntervalUnion] representing all [T] values in this interval,
     * excluding all [T] values in the specified [interval].
     */
    operator fun minus( interval: Interval<T, TSize> ): IntervalUnion<T, TSize>
    {
        val subtract = interval.nonReversed()
        val `this` = nonReversed()

        val lowerBoundCompare: Int = `this`.start.compareTo( subtract.end )
        val upperBoundCompare: Int = `this`.end.compareTo( subtract.start )
        val result = MutableIntervalUnion<T, TSize>()

        // When the interval to subtract lies in front or behind, the current interval is unaffected.
        if ( lowerBoundCompare > 0 || upperBoundCompare < 0 )
        {
            result.add( this )
            return result
        }

        // If the interval to subtract starts after the start of this interval, add the remaining lower bound chunk.
        val startCompare: Int = `this`.start.compareTo( subtract.start )
        if ( startCompare < 0 || ( startCompare == 0 && `this`.isStartIncluded && !subtract.isStartIncluded ) )
        {
            val lowerBoundRemnant =
                Interval( `this`.start, `this`.isStartIncluded, subtract.start, !subtract.isStartIncluded, operations )
            result.add( lowerBoundRemnant )
        }

        // If the interval to subtract ends before the end of this interval, add the remaining upper bound chunk.
        val endCompare: Int = `this`.end.compareTo( subtract.end )
        if ( endCompare > 0 || ( endCompare == 0 && `this`.isEndIncluded && !subtract.isEndIncluded ) )
        {
            val upperBoundRemnant =
                Interval( subtract.end, !subtract.isEndIncluded, `this`.end, `this`.isEndIncluded, operations )
            result.add( upperBoundRemnant )
        }

        return result
    }

    /**
     * Determines whether [interval] has at least one value in common with this interval.
     */
    fun intersects( interval: Interval<T, TSize> ): Boolean
    {
        val interval1 = nonReversed()
        val interval2 = interval.nonReversed()

        val rightOfCompare: Int = interval2.start.compareTo( interval1.end )
        val leftOfCompare: Int = interval2.end.compareTo( interval1.start )
        val liesRightOf =
            rightOfCompare > 0 || ( rightOfCompare == 0 && !(interval2.isStartIncluded && interval1.isEndIncluded) )
        val liesLeftOf =
            leftOfCompare < 0 || ( leftOfCompare == 0 && !(interval2.isEndIncluded && interval1.isStartIncluded) )
        return !( liesRightOf || liesLeftOf )
    }

    /**
     * [reverse] the interval in case [start] is greater than [end] (the interval [isReversed]),
     * or return the interval unchanged otherwise.
     * Either way, the returned interval represents the same set of all [T] values.
     */
    fun nonReversed(): Interval<T, TSize> = if ( isReversed ) reverse() else this

    /**
     * Return an interval representing the same set of all [T] values,
     * but swap [start] and [isStartIncluded] with [end] and [isEndIncluded].
     */
    fun reverse(): Interval<T, TSize> =
        Interval( this.end, this.isEndIncluded, this.start, this.isStartIncluded, operations )

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
