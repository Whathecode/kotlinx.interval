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

        intervalUnionPair( first, nonOverlappingEndPoint )
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
}
