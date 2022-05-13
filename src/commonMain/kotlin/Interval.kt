package com.github.whathecode.kotlinx.interval


/**
 * Represents a set of all [T] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 *
 * @throws IllegalArgumentException if an open or half-open interval with the same start and end value is specified.
 */
data class Interval<T : Comparable<T>>(
    val start: T,
    val isStartIncluded: Boolean,
    val end: T,
    val isEndIncluded: Boolean
)
{
    init
    {
        if ( !isStartIncluded || !isEndIncluded )
        {
            require( start != end ) { "Open or half-open intervals should have differing start and end value." }
        }
    }


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
    inline val isReversed: Boolean get() =  start > end
}


/**
 * Create an [Interval] which includes both [start] and [end].
 */
fun <T : Comparable<T>> createClosedInterval( start: T, end: T ) = Interval( start, true, end, true )

/**
 * Create an [Interval] which excludes both [start] and [end].
 *
 * @throws IllegalArgumentException if an open or half-open interval with the same start and end value is specified.
 */
fun <T : Comparable<T>> createOpenInterval( start: T, end: T ) = Interval( start, false, end, false )
