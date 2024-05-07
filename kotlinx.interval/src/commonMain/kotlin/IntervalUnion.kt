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
     * Gets the upper and lower bound of this set, and whether they are included. Or, `null` if the set is empty.
     * Unlike an interval, not all values lying within the upper and lower bound are necessarily part of this set.
     */
    fun getBounds(): Interval<T, TSize>?
}


/**
 * An [IntervalUnion] which new intervals can be added to,
 * as long as they lie after and aren't immediately adjacent to, previously added intervals.
 */
internal class MutableIntervalUnion<T : Comparable<T>, TSize : Comparable<TSize>> : IntervalUnion<T, TSize>
{
    private val intervals: MutableList<Interval<T, TSize>> = mutableListOf()

    override fun iterator(): Iterator<Interval<T, TSize>> = intervals.iterator()

    override fun getBounds(): Interval<T, TSize>?
    {
        if ( isEmpty() ) return null

        val first: Interval<T, TSize> = intervals.first()
        val last: Interval<T, TSize> = intervals.last()
        return Interval( first.start, first.isStartIncluded, last.end, last.isEndIncluded, first.operations )
    }

    fun add( interval: Interval<T, TSize> ) {
        val toAdd = interval.canonicalize()
        val last = intervals.lastOrNull()
        require( last == null || toAdd.followsNonAdjacently( last ) )
            { "The interval doesn't lie after, or is immediately adjacent to, a previously added interval." }

        intervals.add( toAdd )
    }

    private fun Interval<T, TSize>.followsNonAdjacently( interval: Interval<T, TSize> ): Boolean
    {
        val liesAfter = this.start > interval.end ||
            this.start == interval.end && !(this.isStartIncluded && interval.isEndIncluded)
        val spacing = interval.valueOperations.spacing

        return liesAfter &&
            (spacing == null || interval.valueOperations.unsafeSubtract( this.start, interval.end ) > spacing)
    }

    override fun toString(): String = joinToString( prefix = "[", postfix = "]" )
}
