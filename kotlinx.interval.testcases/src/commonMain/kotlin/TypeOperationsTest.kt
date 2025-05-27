package io.github.whathecode.kotlinx.interval.test

import io.github.whathecode.kotlinx.interval.TypeOperations
import kotlin.test.*


/**
 * Tests for [typeOperations] which uses the variables [a] and [b],
 * and expected results of operations as specified by the corresponding test properties (e.g., [aMinusB]).
 */
@Suppress( "FunctionName" )
abstract class TypeOperationsTest<T : Comparable<T>>(
    private val typeOperations: TypeOperations<T>,
    private val a: T,
    private val b: T,
    private val aMinusB: T
)
{
    @Test
    fun additiveIdentity()
    {
        val zero = typeOperations.additiveIdentity
        val result = typeOperations.unsafeSubtract( a, zero )
        assertEquals( a, result )
    }

    @Test
    fun minValue_overflows_on_unsafeSubtract()
    {
        val overflowMin = typeOperations.unsafeSubtract( typeOperations.minValue, a )

        // Types either overflow, or are coerced to min value (e.g. floating points).
        assertTrue( overflowMin > a || overflowMin == typeOperations.minValue )
    }

    @Test
    fun maxValue_overflows_on_unsafeAdd()
    {
        val overflowMax = typeOperations.unsafeAdd( typeOperations.maxValue, a )

        // Types either overflow, or are coerced to max value (e.g. floating points).
        assertTrue( overflowMax < a || overflowMax == typeOperations.maxValue )
    }

    @Test
    fun unsafeAdd()
    {
        val result = typeOperations.unsafeAdd( aMinusB, b )
        assertEquals( a, result )
    }

    @Test
    fun unsafeSubtract_with_differing_values()
    {
        val result = typeOperations.unsafeSubtract( a, b )
        assertEquals( aMinusB, result )
    }

    @Test
    fun unsafeSubtract_value_from_itself_results_in_additive_identity()
    {
        val result = typeOperations.unsafeSubtract( a, a )
        assertEquals( typeOperations.additiveIdentity, result )
    }

    @Test
    fun toDouble_fromDouble_roundtrip_returns_same_value()
    {
        val original = a

        val toDouble = typeOperations.toDouble( original )
        val fromDouble = typeOperations.fromDouble( toDouble )

        assertEquals( original, fromDouble )
    }

    @Test
    fun toDouble_fromDouble_roundtrip_for_maxima()
    {
        val maxima = listOf( typeOperations.minValue, typeOperations.maxValue )
        maxima.forEach {
            val toDouble = typeOperations.toDouble( it )
            val fromDouble = typeOperations.fromDouble( toDouble )
            assertEquals( it, fromDouble )
        }
    }

    @Test
    fun fromDouble_rounds_to_nearest_value()
    {
        // Rounding only needed for evenly-spaced types.
        val spacing = typeOperations.spacing
        if ( spacing == null ) return

        val next = typeOperations.unsafeAdd( a, spacing )
        val aDouble = typeOperations.toDouble( a )
        val nextDouble = typeOperations.toDouble( next )
        val oneThird = (nextDouble - aDouble) / 3
        val closerToA = aDouble + oneThird
        val closerToNext = nextDouble - oneThird

        assertEquals( a, typeOperations.fromDouble( closerToA ) )
        assertEquals( next, typeOperations.fromDouble( closerToNext ) )
    }

    @Test
    fun fromDouble_overflows_past_max()
    {
        val max = typeOperations.maxValue
        val maxDouble = typeOperations.toDouble( max )
        val pastMaxDouble = maxDouble + 1.0

        val overflowMax = typeOperations.fromDouble( pastMaxDouble )

        // Types either overflow, or are coerced to max value (e.g. floating points).
        if ( overflowMax != max ) assertEquals( typeOperations.minValue, overflowMax )
    }

    @Test
    fun fromDouble_overflows_past_min()
    {
        val min = typeOperations.minValue
        val minDouble = typeOperations.toDouble( min )
        val pastMinDouble = minDouble - 1.0

        val overflowMin = typeOperations.fromDouble( pastMinDouble )

        // Types either overflow, or are coerced to min value (e.g. floating points).
        if ( overflowMin != min ) assertEquals( typeOperations.maxValue, overflowMin )
    }
}
