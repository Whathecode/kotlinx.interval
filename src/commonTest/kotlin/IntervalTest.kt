package com.github.whathecode.kotlinx.interval

import kotlin.test.*


/**
 * Tests for [Interval] which creates intervals for testing
 * using [a], which should be smaller than [b], which should be smaller than [c].
 */
interface IntervalTest<T : Comparable<T>, TSize : Comparable<TSize>>
{
    val a: T
    val b: T
    val c: T

    /**
     * Expected size of the interval between [a] and [b].
     */
    val abSize: TSize

    val valueOperations: TypeOperations<T>
    val sizeOperations: TypeOperations<TSize>

    fun createInterval( start: T, isStartIncluded: Boolean, end: T, isEndIncluded: Boolean ): Interval<T, TSize>

    private fun createClosedInterval( start: T, end: T ): Interval<T, TSize> = createInterval( start, true, end, true )
    private fun createOpenInterval( start: T, end: T ): Interval<T, TSize> = createInterval( start, false, end, false )

    /**
     * Create a closed, open, and both half-open intervals using [start] and [end].
     */
    private fun createAllInclusionTypeIntervals( start: T, end: T ): List<Interval<T, TSize>> = listOf(
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
        val abIntervals = createAllInclusionTypeIntervals( a, b ) + createAllInclusionTypeIntervals( b, a )
        abIntervals.forEach { assertEquals( abSize, it.size ) }
    }

    @Test
    fun size_for_empty_interval_is_zero()
    {
        val zero = sizeOperations.additiveIdentity
        val emptyInterval = createClosedInterval( a, a )
        assertEquals( zero, emptyInterval.size )
    }

    @Test
    fun size_can_be_greater_than_max_value()
    {
        val fullRange = createClosedInterval( valueOperations.minValue, valueOperations.maxValue ).size
        val identity = valueOperations.additiveIdentity
        val rangeBelowIdentity = createClosedInterval( valueOperations.minValue, identity ).size
        val rangeAboveIdentity = createClosedInterval( identity, valueOperations.maxValue ).size

        assertEquals(
            fullRange,
            sizeOperations.unsafeAdd( rangeBelowIdentity, rangeAboveIdentity )
        )
    }
}


/**
 * Create an [IntervalTest] for [Interval] with type parameters [T] and [TSize] which creates intervals for testing
 * using [a], which should be smaller than [b], which should be smaller than [c].
 *
 * @throws UnsupportedOperationException if [valueOperations] or [sizeOperations] needs to be specified
 * since no default is supported.
 */
inline fun <reified T : Comparable<T>, reified TSize : Comparable<TSize>> createIntervalTest(
    a: T,
    b: T,
    c: T,
    /**
     * Expected size of the interval between [a] and [b].
     */
    abSize: TSize,
    crossinline createInterval: (T, Boolean, T, Boolean) -> Interval<T, TSize>,
    /**
     * Specify how to access predefined operators of type [T].
     * For basic Kotlin types, this parameter is initialized with a matching default.
     */
    valueOperations: TypeOperations<T> = getBasicTypeOperationsFor(),
    /**
     * Specify how to access predefined operators of type [TSize].
     * For basic Kotlin types, this parameter is initialized with a matching default.
     */
    sizeOperations: TypeOperations<TSize> = getBasicTypeOperationsFor(),
) : IntervalTest<T, TSize> = object : IntervalTest<T, TSize>
{
    override val a: T = a
    override val b: T = b
    override val c: T = c
    override val abSize: TSize = abSize
    override val valueOperations: TypeOperations<T> = valueOperations
    override val sizeOperations: TypeOperations<TSize> = sizeOperations
    override fun createInterval( start: T, isStartIncluded: Boolean, end: T, isEndIncluded: Boolean ) =
        createInterval( start, isStartIncluded, end, isEndIncluded )
}
