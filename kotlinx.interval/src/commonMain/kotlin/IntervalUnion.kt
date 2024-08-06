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
     * Determines whether this [IntervalUnion] represents the same set of values as the [other] union.
     */
    fun setEquals( other: IntervalUnion<T, TSize> ): Boolean
}


/**
 * Create an [IntervalUnion] which represents a set which contains no values.
 */
@Suppress( "UNCHECKED_CAST" )
internal inline fun <T : Comparable<T>, TSize : Comparable<TSize>> emptyIntervalUnion() =
    EmptyIntervalUnion as IntervalUnion<T, TSize>

private data object EmptyIntervalUnion : IntervalUnion<Nothing, Nothing>
{
    override fun getBounds(): Interval<Nothing, Nothing>? = null

    override fun contains( value: Nothing ): Boolean = false

    override fun minus( toSubtract: Interval<Nothing, Nothing> ): IntervalUnion<Nothing, Nothing> = this

    override fun plus( toAdd: Interval<Nothing, Nothing> ): IntervalUnion<Nothing, Nothing> = toAdd

    override fun setEquals( other: IntervalUnion<Nothing, Nothing> ): Boolean = other == this

    override fun iterator(): Iterator<Interval<Nothing, Nothing>> = emptyList<Interval<Nothing, Nothing>>().iterator()
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
