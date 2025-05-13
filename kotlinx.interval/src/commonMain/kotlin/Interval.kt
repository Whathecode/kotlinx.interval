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
) : IntervalUnion<T, TSize>  // Interval is an IntervalUnion with a single interval.
{
    init
    {
        require( lowerBound >= operations.minValue )
            { "Lower bound shouldn't be smaller than ${operations.minValue} to prevent interval size overflows." }
        require( upperBound <= operations.maxValue )
            { "Upper bound shouldn't be greater than ${operations.maxValue} to prevent interval size overflows." }

        if ( !isStartIncluded || !isEndIncluded )
        {
            require( start != end ) { "Open or half-open intervals should have differing start and end value." }
        }
    }

    internal val valueOperations = operations.valueOperations
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
    val size: TSize get() = operations.getDistance( start, end )


    /**
     * Safe initialization of a calculated interval in case loss of precision can cause invalid initialization.
     * I.e., coerce values to the minimum and maximum allowed values, and collapse the interval to a single value in
     * case its size is zero.
     */
    private fun safeInterval(
        start: T,
        isStartIncluded: Boolean,
        end: T,
        isEndIncluded: Boolean
    ): Interval<T, TSize>
    {
        val coercedStart = coerceMinMax( start )
        val coercedEnd = coerceMinMax( end )

        val interval =
            if ( coercedStart == coercedEnd ) Interval( coercedStart, true, coercedEnd, true, operations )
            else Interval( coercedStart, isStartIncluded, coercedEnd, isEndIncluded, operations )
        return interval
    }

    private fun coerceMinMax( value: T ) =
        if ( value > operations.maxValue ) operations.maxValue
        else if ( value < operations.minValue ) operations.minValue
        else value

    override fun iterator(): Iterator<Interval<T, TSize>> = listOf( this ).iterator()

    override fun getBounds(): Interval<T, TSize> = this.canonicalize()

    /**
     * Checks whether [value] lies within this interval.
     */
    override operator fun contains( value: T ): Boolean
    {
        val lowerCompare = value.compareTo( lowerBound )
        val upperCompare = value.compareTo( upperBound )

        return ( lowerCompare > 0 || (lowerCompare == 0 && isLowerBoundIncluded) )
            && ( upperCompare < 0 || (upperCompare == 0 && isUpperBoundIncluded) )
    }

    /**
     * Get the value at a given percentage within (0.0–1.0) or outside (< 0.0, > 1.0) of the interval.
     * 0.0 corresponds to [start] and 1.0 to [end].
     * The calculation is performed using [Double] arithmetic and the result is rounded to the to nearest value of [T].
     *
     * @throws ArithmeticException if the resulting value falls outside the range which can be represented by [T].
     */
    fun getValueAt( percentage: Double ): T
    {
        val normalizedPercentage = if ( isReversed ) 1.0 - percentage else percentage
        val shiftLeft = normalizedPercentage < 0
        val absPercentage = if ( shiftLeft ) -normalizedPercentage else normalizedPercentage
        val addToLowerBoundDouble = sizeOperations.toDouble( size ) * absPercentage

        // Throw when the resulting value can't be represented by T.
        if ( shiftLeft || normalizedPercentage > 1 )
        {
            val max = if ( shiftLeft ) operations.minValue else operations.maxValue
            val toMax = operations.getDistance( lowerBound, max )
            val toMaxDouble = sizeOperations.toDouble( toMax )
            if ( addToLowerBoundDouble > toMaxDouble )
                throw ArithmeticException( "The resulting value is out of bounds for this type." )
        }

        val addToLowerBoundSize = sizeOperations.fromDouble( addToLowerBoundDouble )
        return operations.unsafeShift( lowerBound, addToLowerBoundSize, shiftLeft )
    }

    /**
     * Return an [IntervalUnion] representing all [T] values in this interval,
     * excluding all [T] values in the specified interval [toSubtract].
     */
    override operator fun minus( toSubtract: Interval<T, TSize> ): IntervalUnion<T, TSize>
    {
        val leftOfCompare: Int = lowerBound.compareTo( toSubtract.upperBound )
        val rightOfCompare: Int = upperBound.compareTo( toSubtract.lowerBound )
        val result = mutableListOf<Interval<T, TSize>>()

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
        if ( upperCompare > 0 || ( upperCompare == 0 && isUpperBoundIncluded && !toSubtract.isUpperBoundIncluded ) )
        {
            val upperBoundRemnant = Interval(
                toSubtract.upperBound, !toSubtract.isUpperBoundIncluded,
                upperBound, isUpperBoundIncluded,
                operations
            )
            result.add( upperBoundRemnant )
        }

        return when ( result.size ) {
            0 -> emptyIntervalUnion()
            1 -> result[ 0 ]
            else -> intervalUnionPair( result[ 0 ], result[ 1 ] )
        }
    }

    /**
     * Return an [IntervalUnion] representing all [T] values in this interval,
     * and all [T] in the specified interval [toAdd].
     */
    override operator fun plus( toAdd: Interval<T, TSize> ): IntervalUnion<T, TSize>
    {
        // When the intervals are disjoint and non-adjacent, no intervals are merged.
        val pairCompare = IntervalUnionComparison.of( this, toAdd )
        if ( pairCompare.isSplitPair ) return pairCompare.asSplitPair()

        // When one of the intervals contains the other, return the biggest interval.
        if ( pairCompare.isFullyEncompassed ) return pairCompare.lower

        // Partially overlapping interval, so the intervals need to be merged.
        return Interval(
            pairCompare.lower.lowerBound, pairCompare.lower.isLowerBoundIncluded,
            pairCompare.upper.upperBound, pairCompare.upper.isUpperBoundIncluded,
            pairCompare.lower.operations )
    }

    override fun shift( amount: TSize, invertDirection: Boolean ): ShiftResult<Interval<T, TSize>, TSize>
    {
        val sizeZero = sizeOperations.additiveIdentity
        if ( amount == sizeZero ) return ShiftResult( this, amount )

        // Clamp maximum amount to shift to min/max value which can be represented by values of T.
        val shiftRight = if ( amount >= sizeZero ) !invertDirection else invertDirection
        val valueZero = valueOperations.additiveIdentity
        val (max, bound) =
            if ( shiftRight ) Pair( operations.maxValue, upperBound )
            else Pair( operations.minValue, lowerBound )
        var maxSize = operations.getDistance( valueZero, max )
        val boundSize = operations.getDistance( valueZero, bound )
        if ( amount < sizeZero )
        {
            maxSize = sizeOperations.unsafeSubtract( sizeZero, maxSize )
        }
        val maxShift =
            if ( (bound < valueZero && shiftRight) || (bound > valueZero && !shiftRight) )
            {
                sizeOperations.unsafeAdd( maxSize, boundSize )
            }
            else
            {
                sizeOperations.unsafeSubtract( maxSize, boundSize )
            }
        val overflows = if ( amount < sizeZero ) amount <= maxShift else amount >= maxShift
        val toShift = if ( overflows ) maxShift else amount

        // Return shifted interval.
        val shiftedStart = operations.unsafeShift( start, toShift, invertDirection )
        val shiftedEnd = operations.unsafeShift( end, toShift, invertDirection )
        val shifted = safeInterval( shiftedStart, isStartIncluded, shiftedEnd, isEndIncluded )
        return ShiftResult( shifted, toShift )
    }

    /**
     * Determines whether [interval] has at least one value in common with this interval.
     */
    override fun intersects( interval: Interval<T, TSize> ): Boolean
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
     * Returns the canonical form of the set of all [T] values represented by this interval.
     * The canonical form is [nonReversed], and for evenly-spaced types (e.g., integers) turns exclusive bounds
     * into inclusive bounds. E.g. The canonical form of [5, 1) is [2, 5].
     */
    fun canonicalize(): Interval<T, TSize>
    {
        // Only evenly-spaced types with exclusive bounds can do more than reversing the interval if needed.
        val spacing = valueOperations.spacing
        if ( spacing == null || (isStartIncluded && isEndIncluded) ) return nonReversed()

        val start = if ( isLowerBoundIncluded ) lowerBound else valueOperations.unsafeAdd( lowerBound, spacing )
        val end = if ( isUpperBoundIncluded ) upperBound else valueOperations.unsafeSubtract( upperBound, spacing )
        return Interval( start, true, end, true, operations )
    }

    /**
     * Determines whether this interval represents the same set of values as the [other] interval.
     *
     * Intervals with differing constructor parameters may still represent the same values,
     * e.g., when they are reversed. For exact equality, use [equals].
     */
    fun setEquals( other: Interval<T, TSize> ): Boolean = this.canonicalize() == other.canonicalize()

    override fun setEquals( other: IntervalUnion<T, TSize> ): Boolean
    {
        val otherInterval = other.singleOrNull() ?: return false
        return setEquals( otherInterval )
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

    override fun toString(): String
    {
        val left = if ( isStartIncluded ) "[" else "("
        val right = if ( isEndIncluded ) "]" else ")"
        return "$left$start, $end$right"
    }
}
