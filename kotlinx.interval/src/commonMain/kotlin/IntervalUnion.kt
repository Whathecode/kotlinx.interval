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
