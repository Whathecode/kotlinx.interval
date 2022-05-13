package com.github.whathecode.kotlinx.interval


/**
 * Represents a set of all [T] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 *
 * @throws IllegalArgumentException if an open or half-open interval with the same start and end value is specified.
 */
abstract class Interval<T : Comparable<T>>(
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

    /**
     * Determines whether this interval equals [other]'s constructor parameters exactly,
     * i.e., not whether they represent the same set of [T] values, such as matching inverse intervals.
     */
    override fun equals( other: Any? ): Boolean
    {
        if ( other !is Interval<*> ) return false

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


    /**
     * Subtract [b] from [a].
     */
    protected abstract fun subtract( a: T, b: T ): T
}
