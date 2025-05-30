package io.github.whathecode.kotlinx.interval.test

import io.github.whathecode.kotlinx.interval.Interval
import io.github.whathecode.kotlinx.interval.IntervalTypeOperations
import io.github.whathecode.kotlinx.interval.IntervalUnion
import kotlin.test.*


/**
 * Tests for [Interval] which creates intervals for testing using [a], which should be smaller than [b],
 * which should be smaller than [c]. The distance between [a] and [b], and [b] and [c], should be equal.
 * In addition, for evenly-spaced types of [T], this distance should be greater than the spacing between any two
 * subsequent values in the set.
 * And, for non-evenly-spaced types, this distance should be small enough so that there isn't sufficient precision to
 * represent them as individual values when shifted close to the max possible values represented by [T].
 */
@Suppress( "FunctionName" )
abstract class IntervalTest<T : Comparable<T>, TSize : Comparable<TSize>>(
    private val a: T,
    private val b: T,
    private val c: T,
    /**
     * Expected size of the interval between [a] and [b]. Should be positive.
     */
    private val abSize: TSize,
    /**
     * Provide access to the predefined set of operators of [T] and [TSize] and conversions between them.
     */
    private val operations: IntervalTypeOperations<T, TSize>
) : IntervalTypeOperationsTest<T, TSize>( operations, abSize )
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
        assertTrue( abSize > sizeOperations.additiveIdentity )

        // The distance between a-b and b-c should be equal.
        val abSize = valueOperations.unsafeSubtract( b, a )
        val bcSize = valueOperations.unsafeSubtract( c, b )
        assertTrue( abSize == bcSize )

        // For evenly-spaced types, the distance should be e greater than the spacing.
        val spacing = valueOperations.spacing
        if ( spacing != null ) assertTrue( abSize > spacing )
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
    fun getValueAt_inside_interval()
    {
        val acIntervals = createAllInclusionTypeIntervals( a, c )

        for ( ac in acIntervals )
        {
            assertEquals( a,ac.getValueAt( 0.0 ) )
            assertEquals( b, ac.getValueAt( 0.5 ) )
            assertEquals( c, ac.getValueAt( 1.0 ) )
        }
    }

    @Test
    fun getValueAt_outside_interval()
    {
        val abIntervals = createAllInclusionTypeIntervals( a, b )
        for ( ab in abIntervals ) assertEquals( c, ab.getValueAt( 2.0 ) )

        val bcIntervals = createAllInclusionTypeIntervals( b, c )
        for ( bc in bcIntervals ) assertEquals( a, bc.getValueAt( -1.0 ) )
    }

    @Test
    fun getValueAt_reverse_intervals()
    {
        val adIntervals = createAllInclusionTypeIntervals( a, d )

        for ( ad in adIntervals )
        {
            val nonReversed = ad.getValueAt( 0.2 )
            val reversed = ad.reverse().getValueAt( 0.8 )
            assertEquals(
                valueOperations.toDouble( nonReversed ),
                valueOperations.toDouble( reversed ),
                absoluteTolerance = 0.000000001
            )
        }
    }

    @Test
    fun getValueAt_returned_value_overflows()
    {
        val maxIntervals = createAllInclusionTypeIntervals( operations.minValue, operations.maxValue )

        for ( max in maxIntervals )
        {
            assertEquals( max.start, max.getValueAt( 0.0 ) )
            // Loss of precision for large values as part of double conversion is expected.
            // Therefore, compare double-converted values which have the same loss of precision.
            assertEquals(
                valueOperations.toDouble(max.end ),
                valueOperations.toDouble( max.getValueAt( 1.0 ) )
            )
            assertFailsWith<ArithmeticException> { max.getValueAt( -0.1 ) }
            assertFailsWith<ArithmeticException> { max.getValueAt( 1.1 ) }
        }
    }

    @Test
    fun getValueAt_percentage_overflows()
    {
        val ab = createClosedInterval( a, b )

        val maxPercentage = sizeOperations.toDouble( sizeOperations.maxValue ) / sizeOperations.toDouble( ab.size )
        val tooBigPercentage = maxPercentage * 2

        assertFailsWith<ArithmeticException> { ab.getValueAt( tooBigPercentage ) }
    }

    @Test
    fun getPercentageFor_inside_interval()
    {
        val acIntervals = createAllInclusionTypeIntervals( a, c )

        for ( ac in acIntervals )
        {
            assertEquals( 0.0, ac.getPercentageFor( a ) )
            assertEquals( 0.5, ac.getPercentageFor( b ) )
            assertEquals( 1.0, ac.getPercentageFor( c ) )
        }
    }

    @Test
    fun getPercentageFor_outside_interval()
    {
        val bcIntervals = createAllInclusionTypeIntervals( b, c )
        for ( bc in bcIntervals ) assertEquals( -1.0, bc.getPercentageFor( a ) )

        val abIntervals = createAllInclusionTypeIntervals( a, b )
        for ( ab in abIntervals ) assertEquals( 2.0, ab.getPercentageFor( c ) )
    }

    @Test
    fun getPercentageFor_reverse_intervals()
    {
        val adIntervals = createAllInclusionTypeIntervals( a, d )
        for ( ad in adIntervals )
        {
            // a, b, c, and d all lie the same distance apart, so b and c are 1/4th of the bounds away.
            val nonReversed = ad.getPercentageFor( b )
            assertEquals( 0.333, nonReversed, absoluteTolerance = 0.001 )
            val reversed = ad.reverse().getPercentageFor( b )
            assertEquals( 0.666, reversed, absoluteTolerance = 0.001 )
        }
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
    fun shift_succeeds()
    {
        val bcIntervals = createAllInclusionTypeIntervals( b, c )
        val shiftSize = abSize
        val expectedShiftSize: T = valueOperations.unsafeSubtract( b, a )

        for ( bc in bcIntervals )
        {
            val shifted = bc.shift( shiftSize )
            val expectedInterval = createInterval(
                valueOperations.unsafeAdd( b, expectedShiftSize ),
                bc.isStartIncluded,
                valueOperations.unsafeAdd( c, expectedShiftSize ),
                bc.isEndIncluded
            )
            assertEquals( expectedInterval, shifted.shiftedInterval )
            assertEquals( shiftSize, shifted.offsetAmount )
            assertEquals( bc.size, shifted.shiftedInterval.size )
        }
    }

    @Test
    fun shift_using_invertedDirection_succeeds()
    {
        val bcIntervals = createAllInclusionTypeIntervals( b, c )
        val shiftSize = abSize
        val expectedShiftSize: T = valueOperations.unsafeSubtract( b, a )

        for ( bc in bcIntervals )
        {
            val shifted = bc.shift( shiftSize, invertDirection = true )
            val expectedInterval = createInterval(
                a,
                bc.isStartIncluded,
                valueOperations.unsafeSubtract( c, expectedShiftSize ),
                bc.isEndIncluded
            )
            assertEquals( expectedInterval, shifted.shiftedInterval )
            assertEquals( shiftSize, shifted.offsetAmount )
            assertEquals( bc.size, shifted.shiftedInterval.size )
        }
    }

    @Test
    fun shift_negative_amount_equals_shift_positive_amount_using_invertedDirection()
    {
        if ( !sizeOperations.isSignedType ) return

        val bc = createClosedInterval( b, c )
        val abSizeNegative = sizeOperations.unsafeSubtract( sizeOperations.additiveIdentity, abSize )
        val shiftNegative = bc.shift( abSizeNegative )

        val shiftPositive = bc.shift( abSize, invertDirection = true )
        assertEquals( shiftPositive.shiftedInterval, shiftNegative.shiftedInterval )
        assertEquals( abSizeNegative, shiftNegative.offsetAmount )
    }

    @Test
    fun shift_by_zero_returns_same_interval()
    {
        val toShift = createAllInclusionTypeIntervals( a, b )
        for ( original in toShift )
        {
            val zero = sizeOperations.additiveIdentity

            val shiftResult = original.shift( zero )
            assertSame( original, shiftResult.shiftedInterval )

            val shiftResultInverted = original.shift( zero, invertDirection = true )
            assertSame( original, shiftResultInverted.shiftedInterval )
        }
    }

    @Test
    fun shift_with_overflow_shifts_up_to_maximum_value()
    {
        val bottomHalf = createClosedInterval( operations.minValue, valueOperations.additiveIdentity )
        val maxRange = operations.getDistance( operations.minValue, operations.maxValue )

        val shifted = bottomHalf.shift( maxRange )

        assertEquals( operations.maxValue, shifted.shiftedInterval.upperBound )
        assertEquals( bottomHalf.size, shifted.shiftedInterval.size )
        val expectedShift = sizeOperations.unsafeSubtract( maxRange, bottomHalf.size )
        assertEquals( expectedShift, shifted.offsetAmount )
    }

    @Test
    fun shift_using_invertedDirection_with_overflow_shifts_down_to_minimum_value()
    {
        val upperHalf = createClosedInterval( valueOperations.additiveIdentity, operations.maxValue )
        val maxRange = operations.getDistance( operations.minValue, operations.maxValue )

        val shifted = upperHalf.shift( maxRange, invertDirection = true )

        assertEquals( operations.minValue, shifted.shiftedInterval.lowerBound )
        assertEquals( upperHalf.size, shifted.shiftedInterval.size )
        val expectedShift = sizeOperations.unsafeSubtract( maxRange, upperHalf.size )
        assertEquals( expectedShift, shifted.offsetAmount )
    }

    @Test
    fun shift_tiny_intervals_long_distances_for_value_types_with_lossy_precision()
    {
        if ( valueOperations.spacing != null ) return // Evenly-spaced types don't lose precision.

        val tinyInterval = createOpenInterval( a, b )
        val shifted = tinyInterval.shift( sizeOperations.maxValue )

        // Due to loss of precision, there is no more distinction between `a` and `b` after shifting, resulting in an
        // interval with `size` 0. This causes the interval to "collapse" into a single value, represented as a closed
        // interval.
        assertTrue( shifted.shiftedInterval.isClosedInterval )
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
