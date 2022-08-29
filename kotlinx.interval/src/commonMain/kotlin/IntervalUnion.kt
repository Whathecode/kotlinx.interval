package io.github.whathecode.kotlinx.interval


/**
 * Represents a set of all [T] values contained in a collection of non-overlapping [Interval]s,
 * stored in normalized form and ordered by their start values.
 */
sealed interface IntervalUnion<T : Comparable<T>, TSize : Comparable<TSize>> : Iterable<Interval<T, TSize>>


/**
 * An [IntervalUnion] which new intervals can be added to,
 * as long as they are in normalized form, lie after, and don't overlap with previously added intervals.
 */
internal class MutableIntervalUnion<T : Comparable<T>, TSize : Comparable<TSize>> : IntervalUnion<T, TSize>
{
    private val intervals: MutableList<Interval<T, TSize>> = mutableListOf()

    override fun iterator(): Iterator<Interval<T, TSize>> = intervals.iterator()

    fun add( interval: Interval<T, TSize> )
    {
        require( !interval.isReversed ) { "The interval is reversed. Normalized form is required." }
        require( intervals.all { it.start < interval.start } )
            { "The interval lies before a previously added interval." }
        val last = intervals.lastOrNull()
        require(
            last == null ||
            (interval.start > last.end || interval.start == last.end && interval.isStartIncluded != last.isEndIncluded)
        ) { "The interval overlaps with a previously added interval." }

        intervals.add( interval )
    }
}
