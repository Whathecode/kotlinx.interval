package com.github.whathecode.kotlinx.interval

import kotlin.test.*


/**
 * Tests for [Interval] which creates intervals for testing
 * using [a], which should be smaller than [b], which should be smaller than [c].
 */
abstract class IntervalTest<T : Comparable<T>>( val a: T, val b: T, val c: T )
{
    init { require( a < b && b < c ) }

    /**
     * Create a closed, open, and both half-open intervals using [start] and [end].
     */
    private fun createAllInclusionTypeIntervals( start: T, end: T ): List<Interval<T>> = listOf(
        createClosedInterval( start, end ),
        createOpenInterval( start, end ),
        Interval( start, true, end, false ),
        Interval( start, false, end, true )
    )


    @Test
    fun constructing_open_or_half_open_intervals_with_same_start_and_end_fails()
    {
        assertFailsWith<IllegalArgumentException> { createOpenInterval( a, a ) }
        assertFailsWith<IllegalArgumentException> { Interval( a, true, a, false ) }
        assertFailsWith<IllegalArgumentException> { Interval( a, false, a, true ) }
    }

    @Test
    fun isOpenInterval()
    {
        val openInterval = createOpenInterval( a, b )
        assertTrue( openInterval.isOpenInterval )

        val notOpen = createAllInclusionTypeIntervals( a, b ) - openInterval
        notOpen.forEach { assertFalse( it.isOpenInterval ) }
    }

    @Test
    fun isClosedInterval()
    {
        val closedInterval = createClosedInterval( a, b )
        assertTrue( closedInterval.isClosedInterval )

        val notClosed = createAllInclusionTypeIntervals( a, b ) - closedInterval
        notClosed.forEach { assertFalse( it.isClosedInterval ) }
    }

    @Test
    fun isReversed()
    {
        val notReversed = createAllInclusionTypeIntervals( a, b ) + createClosedInterval( a, a )
        notReversed.forEach { assertFalse( it.isReversed ) }

        val reversed = createAllInclusionTypeIntervals( b, a )
        reversed.forEach { assertTrue( it.isReversed ) }
    }
}
