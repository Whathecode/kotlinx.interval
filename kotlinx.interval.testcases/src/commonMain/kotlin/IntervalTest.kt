package io.github.whathecode.kotlinx.interval.test

import io.github.whathecode.kotlinx.interval.Interval
import io.github.whathecode.kotlinx.interval.IntervalTypeOperations
import io.github.whathecode.kotlinx.interval.IntervalUnion
import kotlin.test.*


/**
 * Tests for [Interval] which creates intervals for testing using [a], which should be smaller than [b],
 * which should be smaller than [c].
 * For evenly-spaced types of [T], the distance between [a] and [b], and [b] and [c], should be greater than the spacing
 * between subsequent values in the set.
 */
@Suppress( "FunctionName" )
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

        // For evenly-spaced types, the distance between a-b, and b-c, should be greater than the spacing.
        val spacing = valueOperations.spacing
        if ( spacing != null )
        {
            val abSize = valueOperations.unsafeSubtract( b, a )
            val bcSize = valueOperations.unsafeSubtract( c, b )
            assertTrue( abSize > spacing && bcSize > spacing )
        }
    }

    @Test
    fun has_valid_type_operations()
    {
        // Minimum allowed value of T can be converted back and forth using TSize.
        val min: T = operations.minValue
        val minSize: TSize = operations.getDistanceTo( min )
        val minSizeValue: T = operations.unsafeValueAt( minSize )
        val subtractedMinSize: T = valueOperations.unsafeSubtract( valueOperations.additiveIdentity, minSizeValue )
        assertEquals( min, subtractedMinSize )

        // Maximum allowed value of T can be converted back and forth using TSize.
        val max: T = operations.maxValue
        val maxSize: TSize = operations.getDistanceTo( max )
        val maxSizeValue: T = operations.unsafeValueAt( maxSize )
        assertEquals( max, maxSizeValue )
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
    fun size_for_interval_with_one_value_is_zero()
    {
        val zero = sizeOperations.additiveIdentity
        val oneValue = createClosedInterval( a, a )
        assertEquals( zero, oneValue.size )
    }

    @Test
    fun size_can_be_greater_than_max_value()
    {
        val fullRange = createClosedInterval( operations.minValue, operations.maxValue ).size
        val identity = valueOperations.additiveIdentity
        val rangeBelowIdentity = createClosedInterval( operations.minValue, identity ).size
        val rangeAboveIdentity = createClosedInterval( identity, operations.maxValue ).size

        assertEquals(
            fullRange,
            sizeOperations.unsafeAdd( rangeBelowIdentity, rangeAboveIdentity )
        )
    }

    @Test
    fun getBounds_returns_canonicalized_interval()
    {
        createAllInclusionTypeIntervals( a, b )
            .forEach { assertEquals( it.canonicalize(), it.getBounds() ) }
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
            assertUnionEquals( expected, ad - bc )
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
                ac - bd
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
            createClosedInterval( a, a ),
            (abWithA - abWithoutA).singleOrNull()
        )

        val abWithB = createClosedInterval( a, b )
        val abWithoutB = createInterval ( a, true, b, false )
        assertEquals(
            createClosedInterval( b, b ),
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
            (abWithB - bcWithB)
        )

        val bcWithoutB = createOpenInterval( b, c )
        assertEquals( abWithB, (abWithB - bcWithoutB).singleOrNull() )
    }

    @Test
    fun plus_for_interval_lying_within()
    {
        val adIntervals = createAllInclusionTypeIntervals( a, d )
        val bcIntervals = createAllInclusionTypeIntervals( b, c )

        for ( ad in adIntervals ) for ( bc in bcIntervals )
            assertEquals( ad, (ad + bc).singleOrNull() )
    }

    @Test
    fun plus_for_partial_overlapping_interval()
    {
        val acIntervals = createAllInclusionTypeIntervals( a, c )
        val bdIntervals = createAllInclusionTypeIntervals( b, d )

        for ( ac in acIntervals ) for ( bd in bdIntervals )
        {
            assertEquals(
                createInterval( a, ac.isStartIncluded, d, bd.isEndIncluded ),
                (ac + bd).singleOrNull()
            )
        }
    }

    @Test
    fun plus_for_partial_overlapping_reversed_interval()
    {
        val acIntervals = createAllInclusionTypeIntervals( a, c ).map { it.reverse() }
        val bdIntervals = createAllInclusionTypeIntervals( b, d ).map { it.reverse() }

        for ( ac in acIntervals ) for ( bd in bdIntervals )
        {
            assertEquals(
                createInterval( a, ac.isLowerBoundIncluded, d, bd.isUpperBoundIncluded ),
                (ac + bd).singleOrNull()
            )
        }
    }

    @Test
    fun plus_for_matching_interval()
    {
        val abIntervals = createAllInclusionTypeIntervals( a, b )

        for ( ab in abIntervals )
            assertEquals( ab, (ab + ab).singleOrNull() )
    }

    @Test
    fun plus_for_encompassing_interval()
    {
        val bcIntervals = createAllInclusionTypeIntervals( b, c )
        val adIntervals = createAllInclusionTypeIntervals( a, d )

        for ( bc in bcIntervals ) for ( ad in adIntervals )
            assertEquals( ad, (bc + ad).singleOrNull() )
    }

    @Test
    fun plus_for_nonoverlapping_interval()
    {
        val abIntervals = createAllInclusionTypeIntervals( a, b )
        val cdIntervals = createAllInclusionTypeIntervals( c, d )

        for ( ab in abIntervals ) for ( cd in cdIntervals )
        {
            assertUnionEquals( setOf( ab, cd ), ab + cd )
            assertUnionEquals( setOf( ab, cd ), cd + ab )
        }
    }

    @Test
    fun plus_for_overlapping_intervals_with_touching_endpoints()
    {
        val abWithA = createClosedInterval( a, b )
        val abWithoutA = createInterval( a, false, b, true )
        assertEquals( abWithA, (abWithA + abWithoutA).singleOrNull() )

        val abWithB = createClosedInterval( a, b )
        val abWithoutB = createInterval ( a, true, b, false )
        assertEquals( abWithB, (abWithB + abWithoutB).singleOrNull() )

        val abClosed = createClosedInterval( a, b )
        val abOpen = createOpenInterval( a, b )
        assertEquals( abClosed, (abClosed + abOpen).singleOrNull() )
    }

    @Test
    fun plus_for_neighbouring_interval_with_touching_endpoints()
    {
        val abWithB = createClosedInterval( a, b )
        val bcWithB = createClosedInterval( b, c )
        assertEquals(
            createClosedInterval( a, c ),
            (abWithB + bcWithB).singleOrNull()
        )

        val bcWithoutB = createOpenInterval( b, c )
        assertEquals(
            createInterval( a, true, c, false ),
            (abWithB + bcWithoutB).singleOrNull()
        )
    }

    @Test
    fun plus_for_adjacent_intervals()
    {
        // Don't test non-evenly-spaced types.
        val spacing = valueOperations.spacing ?: return

        val ab = createClosedInterval( a, b )
        val bNext = valueOperations.unsafeAdd( b, spacing )
        val bNextC = createClosedInterval( bNext, c )

        val expected = createClosedInterval( a, c )
        assertEquals( expected, ab + bNextC )
        assertEquals( expected, bNextC + ab )
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
    fun nonReversed_returns_same_instance_when_not_isReversed()
    {
        val normal = createAllInclusionTypeIntervals( a, b )
        for ( original in normal )
        {
            val unchanged = original.nonReversed()
            assertSame( original, unchanged )
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

    @Test
    fun canonicalize_returns_same_instance_if_already_canonical()
    {
        // For evenly-spaced types, only closed intervals are canonical.
        val canonicalIntervals =
            if ( valueOperations.spacing == null ) createAllInclusionTypeIntervals( a, b )
            else listOf( createClosedInterval( a, b ) )

        canonicalIntervals.forEach { assertSame( it, it.canonicalize() ) }
    }

    @Test
    fun canonicalize_reverses_interval_if_reversed()
    {
        // For evenly-spaced types, excluded bounds would also be canonicalized.
        val reversedIntervals =
            if ( valueOperations.spacing == null ) createAllInclusionTypeIntervals( b, a )
            else listOf( createClosedInterval( b, a ) )

        reversedIntervals.forEach { assertEquals( it.reverse(), it.canonicalize() ) }
    }

    @Test
    fun canonicalize_makes_exclusive_bounds_inclusive()
    {
        // Don't test non-evenly-spaced types.
        val spacing = valueOperations.spacing ?: return

        val bPrev = valueOperations.unsafeSubtract( b, spacing )
        val aNext = valueOperations.unsafeAdd( a, spacing )

        val bExclusive = createInterval( a, true, b, false )
        assertEquals( createClosedInterval( a, bPrev ), bExclusive.canonicalize() )

        val aExclusive = createInterval( a, false, b, true )
        assertEquals( createClosedInterval( aNext, b ), aExclusive.canonicalize() )

        val abExclusive = createOpenInterval( a, b )
        assertEquals( createClosedInterval( aNext, bPrev ), abExclusive.canonicalize() )
    }

    @Test
    fun setEquals_returns_true_for_set_with_same_bounds()
    {
        createAllInclusionTypeIntervals( a, b ).forEach {
            val exact = createInterval( it.start, it.isStartIncluded, it.end, it.isEndIncluded )
            assertTrue( it.setEquals( exact ) )

            val reverse = it.reverse()
            assertTrue( it.setEquals( reverse ) )
        }
    }

    @Test
    fun setEquals_returns_false_for_differing_sets()
    {
        val abIntervals = createAllInclusionTypeIntervals( a, b )
        val acIntervals = createAllInclusionTypeIntervals( a, c )
        for ( ab in abIntervals ) for ( ac in acIntervals )
            assertFalse( ab.setEquals( ac ) )
    }

    @Test
    fun setEquals_for_evenly_spaced_types_with_differing_bounds_can_still_equal()
    {
        // Don't test non-evenly-spaced types.
        val spacing = valueOperations.spacing ?: return

        val bNext = valueOperations.unsafeAdd( b, spacing )
        val bPrev = valueOperations.unsafeSubtract( b, spacing )

        // [a, b] == [a, bNext)
        val bEndInclusive = createClosedInterval( a, b )
        val bNextExclusive = createInterval( a, true, bNext, false )
        assertTrue( bEndInclusive.setEquals( bNextExclusive ) )

        // [a, b) != [a, bNext)
        val bEndExclusive = createInterval( a, true, b, false )
        assertFalse( bEndExclusive.setEquals( bNextExclusive ) )

        // [b, c] == (bPrev, c]
        val bStartInclusive = createClosedInterval( b, c )
        val bPrevExclusive = createInterval( bPrev, false, c, true )
        assertTrue( bStartInclusive.setEquals( bPrevExclusive ) )

        // (b, c] != (bPrev, c]
        val bStartExclusive = createInterval( b, false, c, true )
        assertFalse( bStartExclusive.setEquals( bPrevExclusive ) )

        // [b, b] == (bPrev, bNext)
        val justB = createClosedInterval( b, b )
        val bNextPrevExclusive = createInterval( bPrev, false, bNext, false )
        assertTrue( justB.setEquals( bNextPrevExclusive ) )
    }

    @Test
    fun toString_matches_common_math_notation()
    {
        val closed = createClosedInterval( a, b )
        assertEquals( "[$a, $b]", closed.toString() )

        val open = createOpenInterval( a, b )
        assertEquals( "($a, $b)", open.toString() )

        val leftHalfOpen = createInterval( a, false, b, true )
        assertEquals( "($a, $b]", leftHalfOpen.toString() )

        val rightHalfOpen = createInterval( a, true, b, false )
        assertEquals( "[$a, $b)", rightHalfOpen.toString() )
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

    /**
     * Used to compare unions with multiple intervals.
     */
    private fun assertUnionEquals( expected: Set<Interval<T, TSize>>, actual: IntervalUnion<T, TSize> )
    {
        require( expected.size > 1 )
            { "This comparison should only be used when multiple intervals are expected." }

        assertEquals( expected, actual.toSet() )
    }
}
