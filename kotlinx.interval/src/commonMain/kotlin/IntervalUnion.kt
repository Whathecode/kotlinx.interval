package io.github.whathecode.kotlinx.interval


/**
 * Represents a set of all [T] values contained in a collection of non-overlapping [Interval]s.
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
 * as long as they are in normalized form, lie after, and don't overlap with previously added intervals.
 */
internal class MutableIntervalUnion<T : Comparable<T>, TSize : Comparable<TSize>> : IntervalUnion<T, TSize>
{
    private val intervals: MutableList<Interval<T, TSize>> = mutableListOf()

    override fun iterator(): Iterator<Interval<T, TSize>> = intervals.iterator()

    override fun getBounds(): Interval<T, TSize>?
    {
        if (isEmpty()) return null

        val first: Interval<T, TSize> = intervals.first()
        val last: Interval<T, TSize> = intervals.last()
        return Interval( first.start, first.isStartIncluded, last.end, last.isEndIncluded, first.operations )
    }

    fun add( interval: Interval<T, TSize> )
    {
        require( !interval.isReversed )
            { "The interval is reversed. Normalized form is required." }
        require( intervals.all { it.start < interval.start } )
            { "The interval lies before a previously added interval." }
        val last = intervals.lastOrNull()
        require( last == null || !interval.intersects( last ) )
            { "The interval overlaps with a previously added interval." }

        intervals.add( interval )
    }
}
