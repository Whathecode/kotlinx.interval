package com.github.whathecode.kotlinx.interval

import kotlin.test.*


/**
 * Tests for [Interval] which creates intervals for testing
 * using [a], which should be smaller than [b], which should be smaller than [c].
 */
interface IntervalTest<T : Comparable<T>>
{
    val a: T
    val b: T
    val c: T

    val typeOperations: TypeOperations<T>

    private fun createInterval( start: T, isStartIncluded: Boolean, end: T, isEndIncluded: Boolean ) =
        Interval( start, isStartIncluded, end, isEndIncluded, typeOperations )

    private fun createClosedInterval( start: T, end: T ): Interval<T> = createInterval( start, true, end, true )
    private fun createOpenInterval( start: T, end: T ): Interval<T> = createInterval( start, false, end, false )

    /**
     * Create a closed, open, and both half-open intervals using [start] and [end].
     */
    private fun createAllInclusionTypeIntervals( start: T, end: T ): List<Interval<T>> = listOf(
        createClosedInterval( start, end ),
        createOpenInterval( start, end ),
        createInterval( start, true, end, false ),
        createInterval( start, false, end, true )
    )


    @Test
    fun is_correct_test_configuration()
    {
        assertTrue( a < b && b < c )
    }

    @Test
    fun constructing_open_or_half_open_intervals_with_same_start_and_end_fails()
    {
        assertFailsWith<IllegalArgumentException> { createOpenInterval( a, a ) }
        assertFailsWith<IllegalArgumentException> { createInterval( a, true, a, false ) }
        assertFailsWith<IllegalArgumentException> { createInterval( a, false, a, true ) }
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

    @Test
    fun size_for_normal_and_reverse_intervals_is_the_same()
    {
        val abSize = typeOperations.unsafeSubtract( b, a )
        val abIntervals = createAllInclusionTypeIntervals( a, b ) + createAllInclusionTypeIntervals( b, a )
        abIntervals.forEach { assertEquals( abSize, it.size ) }
    }

    @Test
    fun size_for_empty_interval_is_zero()
    {
        val zero = typeOperations.additiveIdentity
        val emptyInterval = createClosedInterval( a, a )
        assertEquals( zero, emptyInterval.size )
    }
}


/**
 * Create an [IntervalTest] for type [T] which creates intervals for testing
 * using [a], which should be smaller than [b], which should be smaller than [c]
 *
 * @throws UnsupportedOperationException if [typeOperations] needs to be specified since no default is supported.
 */
inline fun <reified T : Comparable<T>> createIntervalTest(
    a: T,
    b: T,
    c: T,
    /**
     * Specify how to access predefined operators of type [T].
     * For basic Kotlin types, this parameter is initialized with a matching default.
     */
    typeOperations: TypeOperations<T> = getBasicTypeOperationsFor()
) : IntervalTest<T> = object : IntervalTest<T>
{
    override val a: T = a
    override val b: T = b
    override val c: T = c
    override val typeOperations: TypeOperations<T> = typeOperations
}
