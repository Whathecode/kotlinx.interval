package io.github.whathecode.kotlinx.interval


/**
 * Represents a set of all [T] values lying between a provided [start] and [end] value.
 * [TSize] is the type used to represent the distance between [T] values.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 * But, it can never be empty. Empty sets are represented as [IntervalUnion], with no containing intervals, instead.
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
    internal val operations: IntervalTypeOperations<T, TSize>
) : IntervalUnion<T, TSize>
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
     * The largest value which is smaller than or equal to all values in the interval (tight lower bound).
     * This corresponds to [start] or [end], depending on which value is smallest.
     */
    inline val lowerBound: T get() = if (isReversed) end else start

    /**
     * Determines whether [lowerBound] is included in the interval.
     */
    inline val isLowerBoundIncluded: Boolean get() = if (isReversed) isEndIncluded else isStartIncluded

    /**
     * The smallest value which is larger than or equal to all values in the interval (tight upper bound).
     * This corresponds to [start] or [end], depending on which value is largest.
     */
    inline val upperBound: T get() = if (isReversed) start else end

    /**
     * Determines whether [upperBound] is included in the interval.
     */
    inline val isUpperBoundIncluded: Boolean get() = if (isReversed) isStartIncluded else isEndIncluded

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
        val lowerCompare = value.compareTo( lowerBound )
        val upperCompare = value.compareTo( upperBound )

        return ( lowerCompare > 0 || (lowerCompare == 0 && isLowerBoundIncluded) )
            && ( upperCompare < 0 || (upperCompare == 0 && isUpperBoundIncluded) )
    }

    /**
     * Return an [IntervalUnion] representing all [T] values in this interval,
     * excluding all [T] values in the specified interval [toSubtract].
     */
    operator fun minus( toSubtract: Interval<T, TSize> ): IntervalUnion<T, TSize>
    {
        val leftOfCompare: Int = lowerBound.compareTo( toSubtract.upperBound )
        val rightOfCompare: Int = upperBound.compareTo( toSubtract.lowerBound )
        val result = MutableIntervalUnion<T, TSize>()

        // When the interval to subtract lies in front or behind, the current interval is unaffected.
        if ( leftOfCompare > 0 || rightOfCompare < 0 ) return this

        // If the interval to subtract starts after the start of this interval, add the remaining lower bound chunk.
        val lowerCompare: Int = lowerBound.compareTo( toSubtract.lowerBound )
        if ( lowerCompare < 0 || ( lowerCompare == 0 && isLowerBoundIncluded && !toSubtract.isLowerBoundIncluded ) )
        {
            val lowerBoundRemnant = Interval(
                lowerBound, isLowerBoundIncluded,
                toSubtract.lowerBound, !toSubtract.isLowerBoundIncluded,
                operations
            )
            result.add( lowerBoundRemnant )
        }

        // If the interval to subtract ends before the end of this interval, add the remaining upper bound chunk.
        val upperCompare: Int = upperBound.compareTo( toSubtract.upperBound )
        if ( upperCompare > 0 || ( upperCompare == 0 && isUpperBoundIncluded && !toSubtract.isEndIncluded ) )
        {
            val upperBoundRemnant = Interval(
                toSubtract.upperBound, !toSubtract.isUpperBoundIncluded,
                upperBound, isUpperBoundIncluded,
                operations
            )
            result.add( upperBoundRemnant )
        }

        return result
    }

    /**
     * Return an [IntervalUnion] representing all [T] values in this interval,
     * and all [T] in the specified interval [toAdd].
     */
    operator fun plus( toAdd: Interval<T, TSize> ): IntervalUnion<T, TSize>
    {
        val leftOfCompare: Int = lowerBound.compareTo( toAdd.upperBound )
        val rightOfCompare: Int = upperBound.compareTo( toAdd.lowerBound )

        // When the interval to add lies in front or behind, no intervals are merged.
        if ( leftOfCompare > 0 || rightOfCompare < 0 )
        {
            return MutableIntervalUnion<T, TSize>().apply {
                add( this@Interval )
                add( toAdd )
            }
        }

        val lowerCompare: Int = lowerBound.compareTo( toAdd.lowerBound )
        val upperCompare: Int = upperBound.compareTo( toAdd.upperBound )

        // When one of the intervals contains the other, return the biggest interval.
        if ( lowerCompare < 0 && upperCompare > 0 ) return this
        if ( lowerCompare > 0 && upperCompare < 0 ) return toAdd

        // Partially overlapping interval, so the intervals need to be merged.
        val lower = if ( lowerCompare <= 0 ) this else toAdd
        val isLowerIncluded = lower.isLowerBoundIncluded || (lowerCompare == 0 && toAdd.isLowerBoundIncluded)
        val upper = if ( upperCompare >= 0 ) this else toAdd
        val isUpperIncluded = upper.isUpperBoundIncluded || (upperCompare == 0 && toAdd.isUpperBoundIncluded)
        return Interval( lower.start, isLowerIncluded, upper.end, isUpperIncluded, operations )
    }

    /**
     * Determines whether [interval] has at least one value in common with this interval.
     */
    fun intersects( interval: Interval<T, TSize> ): Boolean
    {
        val leftOfCompare: Int = interval.upperBound.compareTo( lowerBound )
        val rightOfCompare: Int = interval.lowerBound.compareTo( upperBound )
        val liesLeftOf =
            leftOfCompare < 0 || ( leftOfCompare == 0 && !(interval.isUpperBoundIncluded && isLowerBoundIncluded) )
        val liesRightOf =
            rightOfCompare > 0 || ( rightOfCompare == 0 && !(interval.isLowerBoundIncluded && isUpperBoundIncluded) )
        return !( liesLeftOf || liesRightOf )
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

    // IntervalUnion implementation; Interval is an IntervalUnion with a single interval.
    override fun iterator(): Iterator<Interval<T, TSize>> = listOf( this ).iterator()
    override fun getBounds(): Interval<T, TSize> = this
}
