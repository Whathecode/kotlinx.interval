package io.github.whathecode.kotlinx.interval

import kotlin.test.*


/**
 * Tests for [IntervalUnion].
 */
class MutableIntervalUnionTest
{
    // There is no reason to believe interval unions of different types behave differently.
    // Testing one type should suffice.
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
    fun getBounds_for_multiple_intervals()
    {
        val union = createEmptyUnion()
        val first = interval( 0, 10 )
        val lastHalfOpen = interval( 15,20, isEndIncluded = false )
        union.add( first )
        union.add( lastHalfOpen )

        assertEquals( interval( 0, 20, isEndIncluded = false ), union.getBounds() )
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
    fun add_non_normalized_fails()
    {
        val union = createEmptyUnion()

        val nonNormalized = interval( 10, 0 )
        assertFailsWith<IllegalArgumentException> { union.add( nonNormalized ) }
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
    fun add_non_overlapping_endpoint_succeeds()
    {
        val union = createEmptyUnion()
        union.add( interval( 0, 10 ) )

        val nonOverlappingEndPoint = interval( 10, 20, isStartIncluded = false )
        union.add( nonOverlappingEndPoint )
    }
}
