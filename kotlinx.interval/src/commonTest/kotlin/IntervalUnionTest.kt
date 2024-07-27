package io.github.whathecode.kotlinx.interval

import kotlin.test.*


/**
 * Tests for [emptyIntervalUnion].
 */
class EmptyIntervalUnionTest
{
    private val empty = emptyIntervalUnion<Int, UInt>()

    @Test
    fun isEmpty() = assertTrue( empty.isEmpty() )

    @Test
    fun getBounds_for_empty_union_is_null() = assertEquals( null, empty.getBounds() )

    @Test
    fun plus_returns_added_interval()
    {
        val toAdd = interval( 0, 10 )
        assertEquals( toAdd, (empty + toAdd).singleOrNull() )
    }

    @Test
    fun setEquals_only_other_empty_union() = assertTrue( empty.setEquals( emptyIntervalUnion() ) )
}


/**
 * Tests for [intervalUnionPair].
 */
class IntervalUnionPairTest
{
    @Test
    fun initialize_correctly_ordered_pair()
    {
        val lower = interval( 0, 10 )
        val upper = interval( 20, 30 )

        intervalUnionPair( lower, upper )
    }

    @Test
    fun initialize_incorrectly_ordered_pair_fails()
    {
        val lower = interval( 0, 4 )
        val upper = interval( 5, 10 )

        assertFailsWith<IllegalArgumentException> { intervalUnionPair( upper, lower ) }
    }

    @Test
    fun initialize_with_overlapping_pairs_fails()
    {
        val first = interval( 0, 10 )
        val overlaps = interval( 5, 15 )

        assertFailsWith<IllegalArgumentException> { intervalUnionPair( first, overlaps ) }
    }

    @Test
    fun initialize_adjacent_pairs_with_evenly_spaced_types_fails()
    {
        val first = interval( 0, 10 )
        val adjacent = interval( 11, 20 )

        assertFailsWith<IllegalArgumentException> { intervalUnionPair( first, adjacent ) }
    }

    @Test
    fun initialize_adjacent_pairs_fails()
    {
        val first = interval( 0, 10 )
        val adjacent = interval( 11, 20 )

        assertFailsWith<IllegalArgumentException> { intervalUnionPair( first, adjacent ) }
    }

    @Test
    fun initialize_pairs_with_non_overlapping_endpoint_succeeds()
    {
        val first = interval( 0f, 10f )
        val nonOverlappingEndPoint = interval( 10f, 20f, isStartIncluded = false )

        assertFailsWith<IllegalArgumentException> { intervalUnionPair( first, nonOverlappingEndPoint ) }
    }

    @Test
    fun getBounds_succeeds()
    {
        val union = intervalUnionPair(
            interval( 0, 10 ),
            interval( 15, 20 )
        )

        assertEquals( interval( 0, 20 ), union.getBounds() )
    }

    @Test
    fun getBounds_for_non_canonical_intervals_is_canonicalized()
    {
        val nonCanonicalLower = interval( 10, 0, isEndIncluded = false )
        val nonCanonicalUpper = interval( 20, 15, isStartIncluded = false )
        val union = intervalUnionPair( nonCanonicalLower, nonCanonicalUpper )

        assertEquals( interval( 1, 19 ), union.getBounds() )
    }

    @Test
    fun plus_interval_outside_of_bounds_and_nonadjacent()
    {
        val union = intervalUnionPair( interval( 5, 10 ), interval( 15, 20 ) )

        val liesBefore = interval( 0, 1 )
        assertUnionEquals( setOf( liesBefore ) + union, union + liesBefore )

        val liesBehind = interval( 24, 25 )
        assertUnionEquals( union.toSet() + liesBehind, union + liesBehind )
    }

    @Test
    fun plus_interval_outside_of_bounds_but_adjacent()
    {
        val lower = interval( 5f, 10f )
        val upper = interval( 15f, 20f )
        val union = intervalUnionPair( lower, upper )

        val adjacentBefore = interval( 0f, 5f, isEndIncluded = false )
        val expectMergedLower = interval( 0f, 10f )
        assertUnionEquals( setOf( expectMergedLower, upper ), union + adjacentBefore )

        val adjacentBehind = interval( 20f, 25f, isStartIncluded = false )
        val expectMergedUpper = interval( 15f, 25f )
        assertUnionEquals( setOf( lower, expectMergedUpper ), union + adjacentBehind )
    }

    @Test
    fun plus_interval_outside_of_bounds_but_adjacent_with_evenly_spaced_types()
    {
        val lower = interval( 5, 10 )
        val upper = interval( 15, 20 )
        val union = intervalUnionPair( lower, upper )

        val adjacentBefore = interval( 0, 4 )
        val expectMergedLower = interval( 0, 10 )
        assertUnionEquals( setOf( expectMergedLower, upper ), union + adjacentBefore )

        val adjacentBehind = interval( 21, 25 )
        val expectMergedUpper = interval( 15, 25 )
        assertUnionEquals( setOf( lower, expectMergedUpper ), union + adjacentBehind )
    }

    @Test
    fun plus_for_encompassing_interval()
    {
        val lower = interval( 5, 10 )
        val upper = interval( 15, 20 )
        val union = intervalUnionPair( lower, upper )

        val fullyEncompassing = interval( 0, 25 )
        assertEquals( fullyEncompassing, union + fullyEncompassing )
    }

    @Test
    fun plus_for_interval_encompassing_one_of_the_unions_in_pair()
    {
        val lower = interval( 5, 10 )
        val upper = interval( 15, 20 )
        val union = intervalUnionPair( lower, upper )

        val encompassingLower = interval( 4, 11 )
        assertUnionEquals(
            setOf( interval( 4, 11 ), upper ),
            union + encompassingLower
        )

        val encompassingUpper = interval( 14, 21 )
        assertUnionEquals(
            setOf( lower, interval( 14, 21 ) ),
            union + encompassingUpper
        )
    }

    @Test
    fun plus_for_interval_partially_overlapping_with_both_intervals_in_union()
    {
        val lower = interval( 5, 10 )
        val upper = interval( 15, 20 )
        val union = intervalUnionPair( lower, upper )

        val overlapsBoth = interval( 7, 18 )
        assertEquals(
            interval( 5, 20 ),
            union + overlapsBoth
        )
    }

    @Test
    fun plus_for_interval_partially_overlapping_with_both_unions_in_union()
    {
        val lower1 = interval( 5, 10 )
        val upper1 = interval( 15, 20 )
        val lower2 = interval( 25, 30 )
        val upper2 = interval( 35, 40 )
        val union = intervalUnionPair(
            intervalUnionPair( lower1, upper1 ),
            intervalUnionPair( lower2, upper2 )
        )

        val encompassesInnerIntervals = interval( 12, 32 )
        assertUnionEquals(
            setOf( lower1, encompassesInnerIntervals, upper2 ),
            union + encompassesInnerIntervals
        )

        val overlapsBothInnerIntervals = interval( 18, 28 )
        assertUnionEquals(
            setOf( lower1, interval( 15, 30 ), upper2 ),
            union + overlapsBothInnerIntervals
        )
    }

    @Test
    fun setEquals_with_evenly_spaced_types_succeeds()
    {
        val union = intervalUnionPair(
            interval( 0, 5 ),
            interval( 10, 15, isEndIncluded = false )
        )

        val sameUnion = intervalUnionPair(
            interval( 0, 5 ),
            interval( 10, 14 )
        )
        assertTrue( union.setEquals( sameUnion ) )

        val differentUnion = intervalUnionPair(
            interval( 0, 5 ),
            interval( 10, 15 )
        )
        assertFalse( union.setEquals( differentUnion ) )
    }

    @Test
    fun setEquals_with_non_evenly_spaced_types_succeeds()
    {
        val union = intervalUnionPair(
            interval( 0f, 5f ),
            interval( 10f, 15f, isEndIncluded = false )
        )

        val sameUnion = intervalUnionPair(
            interval( 0f, 5f ),
            interval( 10f, 15f, isEndIncluded = false )
        )
        assertTrue( union.setEquals( sameUnion ) )

        val differentUnion = intervalUnionPair(
            interval( 0f, 5f ),
            interval( 10f, 15f )
        )
        assertFalse( union.setEquals( differentUnion ) )
    }

    @Test
    fun toString_matches_default_list_formatting()
    {
        val union = intervalUnionPair(
            interval( 0, 2 ),
            interval( 4, 8 )
        )

        assertEquals( "[[0, 2], [4, 8]]", union.toString() )
    }

    /**
     * Used to compare unions with multiple intervals.
     */
    private fun<T : Comparable<T>, TSize : Comparable<TSize>> assertUnionEquals(
        expected: Set<Interval<T, TSize>>,
        actual: IntervalUnion<T, TSize>
    )
    {
        require( expected.size > 1 )
            { "This comparison should only be used when multiple intervals are expected." }

        assertEquals( expected, actual.toSet() )
    }
}
