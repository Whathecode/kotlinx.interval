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

    override fun setEquals( other: IntervalUnion<Nothing, Nothing> ): Boolean = other == this

    override fun iterator(): Iterator<Interval<Nothing, Nothing>> = emptyList<Interval<Nothing, Nothing>>().iterator()
}


/**
 * Create an [IntervalUnion] which holds two disjoint, non-adjacent, and non-empty [IntervalUnion]'s.
 */
internal fun <T : Comparable<T>, TSize : Comparable<TSize>> intervalUnionPair(
    lower: IntervalUnion<T, TSize>,
    upper: IntervalUnion<T, TSize>
): IntervalUnion<T, TSize>
{
    val lowerBounds = requireNotNull( lower.getBounds() ) { "Lower union should not be empty." }
    val upperBounds = requireNotNull( upper.getBounds() ) { "Upper union should not be empty." }

    val liesAfter = upperBounds.start > lowerBounds.end ||
        upperBounds.start == lowerBounds.end && !(upperBounds.isStartIncluded && lowerBounds.isEndIncluded)
    val spacing = lowerBounds.valueOperations.spacing
    val followsNonAdjacently = liesAfter &&
        (spacing == null || lowerBounds.valueOperations.unsafeSubtract( upperBounds.start, lowerBounds.end ) > spacing)
    require( followsNonAdjacently )
        { "The upper union doesn't lie after, or is immediately adjacent to, the lower union." }

    val bounds = Interval(
        lowerBounds.start, lowerBounds.isStartIncluded,
        upperBounds.end, upperBounds.isEndIncluded,
        lowerBounds.operations )

    return IntervalUnionPair( lower, upper, bounds )
}

private class IntervalUnionPair<T : Comparable<T>, TSize : Comparable<TSize>>(
    val lower: IntervalUnion<T, TSize>,
    val upper: IntervalUnion<T, TSize>,
    private val bounds: Interval<T, TSize>
) : IntervalUnion<T, TSize>
{
    override fun iterator(): Iterator<Interval<T, TSize>> =
        ( lower.asSequence() + upper.asSequence() ).iterator()

    override fun getBounds(): Interval<T, TSize> = bounds

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
