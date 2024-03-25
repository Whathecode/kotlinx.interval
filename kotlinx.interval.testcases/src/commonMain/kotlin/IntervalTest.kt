package io.github.whathecode.kotlinx.interval.test

import io.github.whathecode.kotlinx.interval.Interval
import io.github.whathecode.kotlinx.interval.IntervalTypeOperations
import kotlin.test.*


/**
 * Tests for [Interval] which creates intervals for testing
 * using [a], which should be smaller than [b], which should be smaller than [c].
 */
abstract class IntervalTest<T : Comparable<T>, TSize : Comparable<TSize>>(
    private val a: T,
    private val b: T,
    private val c: T,
    /**
     * Expected size of the interval between [a] and [b].
     */
    private val abSize: TSize,
    /**
     * Provide access to the predefined set of operators of [T] and [TSize] and conversions between them.
     */
    private val operations: IntervalTypeOperations<T, TSize>
)
{
    private val valueOperations = operations.valueOperations
    private val sizeOperations = operations.sizeOperations

    /**
     * Value which lies as far beyond [c] as [b] lies beyond [a].
     */
    private val d = valueOperations.unsafeAdd( c, valueOperations.unsafeSubtract( b, a ) )

    private fun createInterval( start: T, isStartIncluded: Boolean, end: T, isEndIncluded: Boolean ) =
        Interval( start, isStartIncluded, end, isEndIncluded, operations )
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
    fun lowerBound_and_upperBound()
    {
        val notReversed = createAllInclusionTypeIntervals( a, b )
        notReversed.forEach {
            assertEquals( a, it.lowerBound )
            assertEquals( it.isStartIncluded, it.isLowerBoundIncluded )
            assertEquals( b, it.upperBound )
            assertEquals( it.isEndIncluded, it.isUpperBoundIncluded )
        }

        val reversed = createAllInclusionTypeIntervals( b, a )
        reversed.forEach {
            assertEquals( a, it.lowerBound )
            assertEquals( it.isEndIncluded, it.isLowerBoundIncluded )
            assertEquals( b, it.upperBound )
            assertEquals( it.isStartIncluded, it.isUpperBoundIncluded )
        }

        val single = createClosedInterval( a, a )
        assertEquals( a, single.lowerBound )
        assertEquals( a, single.upperBound )
        assertTrue( single.isLowerBoundIncluded )
        assertTrue( single.isUpperBoundIncluded )
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

    @Test
    fun contains_for_values_within_interval()
    {
        val acIntervals = createAllInclusionTypeIntervals( a, c ) + createAllInclusionTypeIntervals( c, a )
        acIntervals.forEach { assertTrue( b in it ) }
    }

    @Test
    fun contains_for_values_on_endpoints_of_half_open_intervals()
    {
        val onlyAIncluded = listOf(
            createInterval( a, true, b, false ),
            createInterval( b, false, a, true )
        )
        onlyAIncluded.forEach { assertTrue(a in it && b !in it ) }
    }

    @Test
    fun contains_for_values_on_endpoints_of_open_intervals()
    {
        val openIntervals = listOf( createOpenInterval( a, b ), createOpenInterval( b, a ) )
        openIntervals.forEach { assertTrue( a !in it && b !in it ) }
    }

    @Test
    fun minus_for_interval_lying_within()
    {
        val adIntervals = createAllInclusionTypeIntervals( a, d )
        val bcIntervals = createAllInclusionTypeIntervals( b, c )

        for ( ad in adIntervals ) for ( bc in bcIntervals )
        {
            val expected = setOf(
                createInterval( a, ad.isStartIncluded, b, !bc.isStartIncluded ),
                createInterval( c, !bc.isEndIncluded, d, ad.isEndIncluded )
            )
            assertEquals( expected, (ad - bc).toSet() )
        }
    }

    @Test
    fun minus_for_partial_overlapping_interval()
    {
        val acIntervals = createAllInclusionTypeIntervals( a, c )
        val bdIntervals = createAllInclusionTypeIntervals( b, d )

        for ( ac in acIntervals ) for ( bd in bdIntervals )
        {
            assertEquals(
                createInterval( a, ac.isStartIncluded, b, !bd.isStartIncluded ),
                (ac - bd).singleOrNull()
            )
        }
    }

    @Test
    fun minus_for_matching_interval()
    {
        val abIntervals = createAllInclusionTypeIntervals( a, b )

        for ( ab in abIntervals )
            assertTrue( (ab - ab).isEmpty() )
    }

    @Test
    fun minus_for_encompassing_interval()
    {
        val bcIntervals = createAllInclusionTypeIntervals( b, c )
        val adIntervals = createAllInclusionTypeIntervals( a, d )

        for ( bc in bcIntervals ) for ( ad in adIntervals )
            assertTrue( (bc - ad).isEmpty() )
    }

    @Test
    fun minus_for_nonoverlapping_interval()
    {
        val abIntervals = createAllInclusionTypeIntervals( a, b )
        val cdIntervals = createAllInclusionTypeIntervals( c, d )

        for ( ab in abIntervals ) for ( cd in cdIntervals )
            assertEquals( ab, (ab - cd).singleOrNull() )
    }

    @Test
    fun minus_for_overlapping_intervals_with_touching_endpoints()
    {
        val abWithA = createClosedInterval( a, b )
        val abWithoutA = createInterval( a, false, b, true )
        assertEquals(
            createInterval( a, true, a, true ),
            (abWithA - abWithoutA).singleOrNull()
        )

        val abWithB = createClosedInterval( a, b )
        val abWithoutB = createInterval ( a, true, b, false )
        assertEquals(
            createInterval( b, true, b, true ),
            (abWithB - abWithoutB).singleOrNull()
        )

        val abClosed = createClosedInterval( a, b )
        val abOpen = createOpenInterval( a, b )
        assertEquals(
            setOf( createClosedInterval( a, a ), createClosedInterval( b, b ) ),
            (abClosed - abOpen).toSet()
        )
    }

    @Test
    fun minus_for_neighbouring_interval_with_touching_endpoints()
    {
        val abWithB = createClosedInterval( a, b )
        val bcWithB = createClosedInterval( b, c )
        assertEquals(
            createInterval( a, true, b, false ),
            (abWithB - bcWithB).singleOrNull()
        )

        val bcWithoutB = createOpenInterval( b, c )
        assertEquals( abWithB, (abWithB - bcWithoutB).singleOrNull() )
    }

    @Test
    fun intersects_for_fully_contained_intervals()
    {
        val ad = createClosedInterval( a, d )

        val withinIntervals = createAllInclusionTypeIntervals( b, c )
        val onEndPointIntervals = createAllInclusionTypeIntervals( a, b ) + createAllInclusionTypeIntervals( c, d )
        (withinIntervals + onEndPointIntervals).forEach { assertIntersects( ad, it, true ) }
    }

    @Test
    fun intersects_for_partial_overlapping_interval()
    {
        val acIntervals = createAllInclusionTypeIntervals( a, c )
        val bdIntervals = createAllInclusionTypeIntervals( b, d )

        for ( ac in acIntervals ) for ( bd in bdIntervals )
            assertIntersects( ac, bd, true )
    }

    @Test
    fun intersects_for_nonoverlapping_intervals()
    {
        val abIntervals = createAllInclusionTypeIntervals( a, b )
        val cdIntervals = createAllInclusionTypeIntervals( c, d )

        for ( ab in abIntervals ) for ( cd in cdIntervals )
            assertIntersects( ab, cd, false )
    }

    @Test
    fun intersects_for_touching_endpoints()
    {
        val abWithB = createClosedInterval( a, b )
        val bcWithB = createClosedInterval( b, c )
        assertIntersects( abWithB, bcWithB, true )

        val abWithoutB = createOpenInterval( a, b )
        val bcWithoutB = createOpenInterval( b, c )
        assertIntersects( abWithoutB, bcWithoutB, false )

        assertIntersects( abWithB, bcWithoutB, false )
        assertIntersects( abWithoutB, bcWithB, false )
    }

    @Test
    fun nonReversed_reversed_when_isReversed()
    {
        val reversed = createAllInclusionTypeIntervals( b, a )
        for ( original in reversed )
        {
            val normal = original.nonReversed()
            assertEquals( original.reverse(), normal )
        }
    }

    @Test
    fun nonReversed_unchanged_when_not_isReversed()
    {
        val normal = createAllInclusionTypeIntervals( a, b )
        for ( original in normal )
        {
            val unchanged = original.nonReversed()
            assertEquals( original, unchanged )
        }
    }

    @Test
    fun reverse_succeeds()
    {
        val toReverse = createAllInclusionTypeIntervals( a, b )
        for ( original in toReverse )
        {
            val reversed = original.reverse()
            assertEquals( original.start, reversed.end )
            assertEquals( original.isStartIncluded, reversed.isEndIncluded )
            assertEquals( original.end, reversed.start )
            assertEquals( original.isEndIncluded, reversed.isStartIncluded )
        }
    }

    private fun assertIntersects( interval1: Interval<T, TSize>, interval2: Interval<T, TSize>, intersects: Boolean )
    {
        assertEquals( intersects, interval1.intersects( interval2 ) )
        assertEquals( intersects, interval2.intersects( interval1 ) )

        // Reversing intervals should have no effect on whether they intersect or not.
        val interval1Reversed = interval1.reverse()
        val interval2Reversed = interval2.reverse()

        assertEquals( intersects, interval1.intersects( interval2Reversed ) )
        assertEquals( intersects, interval2Reversed.intersects( interval1 ) )

        assertEquals( intersects, interval1Reversed.intersects( interval2 ) )
        assertEquals( intersects, interval2.intersects( interval1Reversed ) )

        assertEquals( intersects, interval1Reversed.intersects( interval2Reversed ) )
        assertEquals( intersects, interval2Reversed.intersects( interval1Reversed ) )
    }
}
