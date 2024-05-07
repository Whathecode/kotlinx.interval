package io.github.whathecode.kotlinx.interval

import kotlin.test.*


/**
 * Tests for [IntervalUnion].
 */
class MutableIntervalUnionTest
{
    // For the majority of tests, testing using an evenly-spaced type suffices.
    // Tests which rely on a non-evenly-spaced type shouldn't use this.
    private fun createEmptyUnion() = MutableIntervalUnion<Int, UInt>()

    @Test
    fun getBounds_for_empty_union_is_null()
    {
        val empty = createEmptyUnion()
        assertEquals( null, empty.getBounds() )
    }

    @Test
    fun getBounds_for_single_interval_equals_interval()
    {
        val union = createEmptyUnion()
        val single = interval( 0, 10 )
        union.add( single )

        assertEquals( single, union.getBounds() )
    }

    @Test
    fun getBounds_for_non_canonical_interval_is_canonicalized()
    {
        val union = createEmptyUnion()
        val nonCanonical = interval( 10, 0, isStartIncluded = false )
        union.add( nonCanonical )

        assertEquals( interval( 0, 9 ), union.getBounds() )
    }

    @Test
    fun getBounds_for_multiple_intervals()
    {
        val union = createEmptyUnion()
        val first = interval( 0, 10 )
        val last = interval( 15, 20 )
        union.add( first )
        union.add( last )

        assertEquals( interval( 0, 20 ), union.getBounds() )
    }

    @Test
    fun add_to_empty_succeeds()
    {
        val union = createEmptyUnion()

        val toAdd = interval( 0, 10 )
        union.add( toAdd )

        assertEquals( toAdd, union.single() )
    }

    @Test
    fun add_to_previous_succeeds()
    {
        val union = createEmptyUnion()
        union.add( interval( 0, 10 ) )

        val liesAfter = interval( 20, 30 )
        union.add( liesAfter )

        assertEquals( liesAfter, union.last() )
    }

    @Test
    fun add_preceding_interval_fails()
    {
        val union = createEmptyUnion()
        union.add( interval( 5, 10 ) )

        val liesBefore = interval( 0, 4 )
        assertFailsWith<IllegalArgumentException> { union.add( liesBefore ) }
    }

    @Test
    fun add_overlapping_interval_fails()
    {
        val union = createEmptyUnion()
        union.add( interval( 0, 10 ) )

        val overlaps = interval( 5, 15 )
        assertFailsWith<IllegalArgumentException> { union.add( overlaps ) }
        val endPointOverlaps = interval( 10, 20 )
        assertFailsWith<IllegalArgumentException> { union.add( endPointOverlaps ) }
    }

    @Test
    fun add_adjacent_interval_fails()
    {
        val union = createEmptyUnion()
        union.add( interval( 0, 10 ) )

        val adjacent = interval( 11, 20 )
        assertFailsWith<IllegalArgumentException> { union.add( adjacent ) }
    }

    @Test
    fun add_non_overlapping_endpoint_succeeds()
    {
        val union = MutableIntervalUnion<Float, Double>()
        union.add( interval( 0f, 10f ) )

        val nonOverlappingEndPoint = interval( 10f, 20f, isStartIncluded = false )
        union.add( nonOverlappingEndPoint )
    }

    @Test
    fun toString_matches_default_list_formatting()
    {
        val union = createEmptyUnion()
        union.add( interval( 0, 2 ) )
        union.add( interval( 4, 8 ) )

        assertEquals( "[[0, 2], [4, 8]]", union.toString() )
    }
}
