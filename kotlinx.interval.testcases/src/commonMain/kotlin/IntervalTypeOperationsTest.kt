package io.github.whathecode.kotlinx.interval.test

import io.github.whathecode.kotlinx.interval.IntervalTypeOperations
import kotlin.test.*


/**
 * Test for [IntervalTypeOperations].
 */
@Suppress( "FunctionName" )
open class IntervalTypeOperationsTest<T : Comparable<T>, TSize : Comparable<TSize>>(
    private val operations: IntervalTypeOperations<T, TSize>,
    private val positiveSize: TSize
)
{
    private val valueOperations = operations.valueOperations
    private val sizeOperations = operations.sizeOperations


    @Test
    fun minValue_can_be_converted_back_and_forth_using_TSize()
    {
        val min: T = operations.minValue
        val minSize: TSize = operations.getDistance( valueOperations.additiveIdentity, min )
        val minSizeValue: T = operations.unsafeValueAt( minSize )
        val subtractedMinSize: T = valueOperations.unsafeSubtract( valueOperations.additiveIdentity, minSizeValue )
        assertEquals( min, subtractedMinSize )
    }

    @Test
    fun maxValue_can_be_converted_back_and_forth_using_TSize()
    {
        val max: T = operations.maxValue
        val maxSize: TSize = operations.getDistance( valueOperations.additiveIdentity, max )
        val maxSizeValue: T = operations.unsafeValueAt( maxSize )
        assertEquals( max, maxSizeValue )
    }

    @Test
    fun getDistance_is_commutative()
    {
        val value = operations.unsafeValueAt( positiveSize )

        val distance1 = operations.getDistance( valueOperations.additiveIdentity, value )
        val distance2 = operations.getDistance( value, valueOperations.additiveIdentity )

        assertEquals( positiveSize, distance1 )
        assertEquals( positiveSize, distance2 )
    }

    @Test
    fun getDistance_is_always_positive()
    {
        if ( !sizeOperations.isSignedType ) return

        val negativeSize = sizeOperations.unsafeSubtract( sizeOperations.additiveIdentity, positiveSize )
        val valueAtNegative = operations.unsafeValueAt( negativeSize )

        val distance1 = operations.getDistance( valueOperations.additiveIdentity, valueAtNegative )
        val distance2 = operations.getDistance( valueAtNegative, valueOperations.additiveIdentity )

        assertEquals( positiveSize, distance1 )
        assertEquals( positiveSize, distance2 )
    }

    @Test
    fun unsafeValueAt_is_always_positive()
    {
        val zero = valueOperations.additiveIdentity

        val valueAtPositive = operations.unsafeValueAt( positiveSize )
        assertTrue( valueAtPositive > zero, "$valueAtPositive isn't > $zero." )

        if ( sizeOperations.isSignedType )
        {
            val negativeSize = sizeOperations.unsafeSubtract( sizeOperations.additiveIdentity, positiveSize )
            val valueAtNegative = operations.unsafeValueAt( negativeSize )
            assertTrue( valueAtNegative > zero, "$valueAtNegative isn't > $zero." )
        }
    }
}
