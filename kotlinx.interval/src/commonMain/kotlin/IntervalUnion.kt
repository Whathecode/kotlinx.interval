package io.github.whathecode.kotlinx.interval


/**
 * Represents a set of all [T] values contained in a collection of disjoint, non-adjacent, [Interval]s.
 * I.e., the intervals don't overlap, and there always lie some values of [T] in between any two intervals.
 * If the collection contains no intervals, it represents an empty set.
 */
sealed interface IntervalUnion<T : Comparable<T>, TSize : Comparable<TSize>> : Iterable<Interval<T, TSize>>
{
    /**
     * Determines whether this is an empty set, i.e., no value of [T] is contained within.
     */
    fun isEmpty(): Boolean = none()

    /**
     * Gets the upper and lower bound of this set, and whether they are included, as a canonical interval.
     * Or, `null` if the set is empty.
     *
     * Unless this set is an [Interval], not all values lying within the upper and lower bound are part of this set.
     */
    fun getBounds(): Interval<T, TSize>?

    /**
     * Checks whether [value] lies within this set.
     */
    operator fun contains( value: T ): Boolean

    /**
     * Return an [IntervalUnion] representing all [T] values in this set,
     * excluding all [T] values in the specified interval [toSubtract].
     */
    operator fun minus( toSubtract: Interval<T, TSize> ): IntervalUnion<T, TSize>

    /**
     * Return an [IntervalUnion] representing all [T] values in this set,
     * and including all [T] in the specified interval [toAdd].
     */
    operator fun plus( toAdd: Interval<T, TSize> ): IntervalUnion<T, TSize>

    /**
     * Returns an [IntervalUnion] offset from this interval union by the specified [amount],
     * or as much as possible before the minimum or maximum value that can be represented by [T] is reached.
     * In case the interval couldn't be offset the full [amount], the final [ShiftResult.offsetAmount] will differ.
     *
     * @param invertDirection Inverts the direction by which the interval is offset. I.e., if [amount] is positive,
     *   shift left instead of shift right, and vice verse. This can be used to shift intervals with unsigned types,
     *   which can't represent a negative [amount], left.
     */
    fun shift( amount: TSize, invertDirection: Boolean = false ): ShiftResult<IntervalUnion<T, TSize>, TSize>

    /**
     * Determines whether [interval] has at least one value in common with this set.
     */
    fun intersects( interval: Interval<T, TSize> ): Boolean

    /**
     * Determines whether this [IntervalUnion] represents the same set of values as the [other] union.
     */
    fun setEquals( other: IntervalUnion<T, TSize> ): Boolean
}


/**
 * The result of an [IntervalUnion.shift] operation on an interval union of type [TInterval].
 */
data class ShiftResult<out TInterval : IntervalUnion<*, TSize>, TSize : Comparable<TSize>>(
    /**
     * A new [IntervalUnion], offset from the interval on which the [IntervalUnion.shift] operation was performed
     * by [offsetAmount].
     */
    val shiftedInterval: TInterval,
    /**
     * The final amount by which [shiftedInterval] is offset from the interval on which the [IntervalUnion.shift]
     * operation was performed. This may be smaller than the originally requested offset in case a larger offset would
     * result in a value which can't be represented by values in the interval.
     */
    val offsetAmount: TSize
)


/**
 * Create an [IntervalUnion] which represents a set which contains no values.
 */
@Suppress( "UNCHECKED_CAST" )
fun <T : Comparable<T>, TSize : Comparable<TSize>> emptyIntervalUnion() = EmptyIntervalUnion as IntervalUnion<T, TSize>

private data object EmptyIntervalUnion : IntervalUnion<Comparable<Any>, Comparable<Any>>
{
    override fun getBounds(): Interval<Comparable<Any>, Comparable<Any>>? = null

    override fun contains( value: Comparable<Any> ): Boolean = false

    override fun minus(
        toSubtract: Interval<Comparable<Any>, Comparable<Any>>
    ): IntervalUnion<Comparable<Any>, Comparable<Any>> = this

    override fun plus(
        toAdd: Interval<Comparable<Any>, Comparable<Any>>
    ): IntervalUnion<Comparable<Any>, Comparable<Any>> = toAdd

    override fun shift(
        amount: Comparable<Any>,
        invertDirection: Boolean
    ): ShiftResult<EmptyIntervalUnion, Comparable<Any>> = ShiftResult( this, amount )

    override fun intersects( interval: Interval<Comparable<Any>, Comparable<Any>> ): Boolean = false

    override fun setEquals( other: IntervalUnion<Comparable<Any>, Comparable<Any>> ): Boolean = other == this

    override fun iterator(): Iterator<Interval<Comparable<Any>, Comparable<Any>>> =
        emptyList<Interval<Comparable<Any>, Comparable<Any>>>().iterator()
}


/**
 * Create an [IntervalUnion] which holds two disjoint, non-adjacent, and non-empty [IntervalUnion]'s.
 *
 * @throws IllegalArgumentException when [union1] or [union2] is empty,
 * or the two pairs are not disjoint and non-adjacent.
 */
internal fun <T : Comparable<T>, TSize : Comparable<TSize>> intervalUnionPair(
    union1: IntervalUnion<T, TSize>,
    union2: IntervalUnion<T, TSize>
): IntervalUnion<T, TSize>
{
    val compare = IntervalUnionComparison.of( union1, union2 )
    return IntervalUnionPair( compare )
}

/**
 * Combines the previously compared [IntervalUnion]'s into a single new [IntervalUnion] containing both
 * (both are returned when iterated), provided that they are disjoint and non-adjacent.
 *
 * @throws IllegalArgumentException when [IntervalUnionComparison.isSplitPair] is false.
 */
internal fun <T : Comparable<T>, TSize : Comparable<TSize>, TUnion : IntervalUnion<T, TSize>>
    IntervalUnionComparison<T, TSize, TUnion>.asSplitPair(): IntervalUnion<T, TSize> = IntervalUnionPair( this )

private class IntervalUnionPair<T : Comparable<T>, TSize : Comparable<TSize>, TUnion : IntervalUnion<T, TSize>>(
    unionPair: IntervalUnionComparison<T, TSize, TUnion>
) : IntervalUnion<T, TSize>
{
    init { require( unionPair.isSplitPair ) { "The pair of unions passed are not disjoint and non-adjacent." } }

    private val lower: TUnion = unionPair.lower
    private val upper: TUnion = unionPair.upper
    private val bounds: Interval<T, TSize> = Interval(
        unionPair.lowerBounds.start, unionPair.lowerBounds.isStartIncluded,
        unionPair.upperBounds.end, unionPair.upperBounds.isEndIncluded,
        unionPair.lowerBounds.operations )

    // Retrieve type operations from any underlying interval.
    private val operations: IntervalTypeOperations<T, TSize> = getOperations( this )
    @Suppress( "UNCHECKED_CAST" )
    private fun getOperations( pair: IntervalUnionPair<*, *, *> ) : IntervalTypeOperations<T, TSize> =
        when ( pair.lower ) {
            is Interval<*, *> -> pair.lower.operations as IntervalTypeOperations<T, TSize>
            is IntervalUnionPair<*, *, *> -> getOperations( pair.lower )
            else -> throw IllegalStateException( "Unexpected underlying interval type: ${pair.lower}." )
        }

    override fun iterator(): Iterator<Interval<T, TSize>> =
        ( lower.asSequence() + upper.asSequence() ).iterator()

    override fun getBounds(): Interval<T, TSize> = bounds

    override fun contains( value: T ): Boolean
    {
        if ( lower is Interval<*, *> && value in lower ) return true
        if ( upper is Interval<*, *> && value in upper ) return true

        // Use bounds check to preempt unnecessary recursion for interval unions.
        if ( lower.getBounds()?.contains( value ) == true ) return value in lower
        if ( upper.getBounds()?.contains( value ) == true ) return value in upper

        return false
    }

    override fun minus( toSubtract: Interval<T, TSize> ): IntervalUnion<T, TSize>
    {
        // When `toSubtract` lies outside this union's bounds, no intervals are affected.
        // When it fully encompasses this union, nothing is left after subtraction.
        val pairCompare = IntervalUnionComparison.of( this, toSubtract )
        if ( pairCompare.isSplitPair ) return this
        if ( pairCompare.isFullyEncompassed && pairCompare.lower == toSubtract ) return emptyIntervalUnion()

        // Ignore `lower` or `upper if it would become empty after subtraction; recurse on the side with a remainder.
        val lowerPairCompare = IntervalUnionComparison.of( lower, toSubtract )
        if ( lowerPairCompare.isFullyEncompassed ) return upper - toSubtract
        val upperPairCompare = IntervalUnionComparison.of( upper, toSubtract )
        if ( upperPairCompare.isFullyEncompassed ) return lower - toSubtract

        // Both `lower` and `upper` have a remainder, so recursively subtract from both sides.
        return intervalUnionPair( lower - toSubtract, upper - toSubtract )
    }

    override fun plus( toAdd: Interval<T, TSize> ): IntervalUnion<T, TSize>
    {
        // When `toAdd` lies outside this union's bounds, prepend or append it.
        // When it fully encompasses this union, `toAdd` replaces it.
        val pairCompare = IntervalUnionComparison.of( this, toAdd )
        if ( pairCompare.isSplitPair ) return pairCompare.asSplitPair()
        if ( pairCompare.isFullyEncompassed && pairCompare.lower == toAdd ) return toAdd

        // When `toAdd` only impacts `lower` or `upper`, recursively add the new interval to the impacted union.
        // Or, if neither are impacted, the new interval must lie in between `lower` and `upper`.
        val lowerPairCompare = IntervalUnionComparison.of( lower, toAdd )
        val upperPairCompare = IntervalUnionComparison.of( upper, toAdd )
        if ( lowerPairCompare.isSplitPair )
        {
            return if ( upperPairCompare.isSplitPair ) intervalUnionPair( lowerPairCompare.asSplitPair(), upper )
            else intervalUnionPair( lower, upper + toAdd )
        }
        else if ( upperPairCompare.isSplitPair ) return intervalUnionPair( lower + toAdd, upper )

        // When both the `lower` and `upper` union are impacted, but either is fully encompassed by `toAdd`,
        // recursively add `toAdd` to the only partially impacted union.
        if ( lowerPairCompare.isFullyEncompassed ) return upper + toAdd
        if ( upperPairCompare.isFullyEncompassed ) return lower + toAdd

        // With all other options excluded, both `lower` and `upper` are partially impacted.
        // TODO: This doesn't make use of known bounds of nested unions in `upper`, only for `lower` through recursion.
        //  This can likely be optimized.
        return upper.fold( lower + toAdd ) { result, upperInterval -> result + upperInterval }
    }

    override fun shift(
        amount: TSize,
        invertDirection: Boolean
    ): ShiftResult<IntervalUnion<T, TSize>, TSize>
    {
        val sizeZero = operations.sizeOperations.additiveIdentity
        val shiftRight = if ( amount >= sizeZero ) !invertDirection else invertDirection

        val lowerShifted: ShiftResult<IntervalUnion<T, TSize>, TSize>
        val upperShifted: ShiftResult<IntervalUnion<T, TSize>, TSize>
        val coercedAmount: TSize
        if ( shiftRight )
        {
            upperShifted = upper.shift( amount, invertDirection )
            lowerShifted = lower.shift( upperShifted.offsetAmount, invertDirection )
            coercedAmount = upperShifted.offsetAmount
        }
        else
        {
            lowerShifted = lower.shift( amount, invertDirection )
            upperShifted = upper.shift( lowerShifted.offsetAmount, invertDirection )
            coercedAmount = lowerShifted.offsetAmount
        }

        return ShiftResult(
            intervalUnionPair( lowerShifted.shiftedInterval, upperShifted.shiftedInterval ),
            coercedAmount
        )
    }

    override fun intersects( interval: Interval<T, TSize> ): Boolean
    {
        if ( lower is Interval<*, *> && lower.intersects( interval ) ) return true
        if ( upper is Interval<*, *> && upper.intersects( interval ) ) return true

        // Use bounds check to preempt unnecessary recursion for interval unions.
        if ( lower.getBounds()?.intersects( interval ) == true ) return lower.intersects( interval )
        if ( upper.getBounds()?.intersects( interval ) == true ) return upper.intersects( interval )

        return false
    }

    override fun setEquals( other: IntervalUnion<T, TSize> ): Boolean
    {
        val iterateThis = map { it.canonicalize() }.iterator()
        val iterateOther = other.map { it.canonicalize() }.iterator()
        while ( iterateThis.hasNext() )
        {
            if ( iterateOther.hasNext() && iterateThis.next() != iterateOther.next() ) return false
        }

        return true
    }

    override fun toString(): String = joinToString( prefix = "[", postfix = "]" )
}
