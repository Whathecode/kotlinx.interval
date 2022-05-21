package com.github.whathecode.kotlinx.interval

import kotlin.test.*


/**
 * Tests for [typeOperations] which uses the variables [a] and [b],
 * and expected results of operations as specified by the corresponding test properties (e.g., [aMinusB]).
 */
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
}
