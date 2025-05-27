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
    private val valueZero = valueOperations.additiveIdentity

    private val sizeOperations = operations.sizeOperations
    private val sizeZero = sizeOperations.additiveIdentity


    @Test
    fun minValue_can_be_converted_back_and_forth_using_TSize()
    {
        val min: T = operations.minValue
        val minSize: TSize = operations.getDistance( valueZero, min )
        val minSizeValue: T = operations.unsafeValueAt( minSize )
        val subtractedMinSize: T = valueOperations.unsafeSubtract( valueZero, minSizeValue )
        assertEquals( min, subtractedMinSize )
    }

    @Test
    fun maxValue_can_be_converted_back_and_forth_using_TSize()
    {
        val max: T = operations.maxValue
        val maxSize: TSize = operations.getDistance( valueZero, max )
        val maxSizeValue: T = operations.unsafeValueAt( maxSize )
        assertEquals( max, maxSizeValue )
    }

    @Test
    fun TSize_can_represent_full_range()
    {
        val rangeBelowZero = operations.getDistance( operations.minValue, valueZero )
        val rangeAboveZero = operations.getDistance( valueZero, operations.maxValue )
        val fullRange = operations.getDistance( operations.minValue, operations.maxValue )

        assertEquals(
            fullRange,
            sizeOperations.unsafeAdd( rangeBelowZero, rangeAboveZero )
        )
    }

    @Test
    fun getDistance_is_commutative()
    {
        val value = operations.unsafeValueAt( positiveSize )

        val distance1 = operations.getDistance( valueZero, value )
        val distance2 = operations.getDistance( value, valueZero )

        assertEquals( positiveSize, distance1 )
        assertEquals( positiveSize, distance2 )
    }

    @Test
    fun getDistance_is_always_positive()
    {
        if ( !sizeOperations.isSignedType ) return

        val negativeSize = sizeOperations.unsafeSubtract( sizeZero, positiveSize )
        val valueAtNegative = operations.unsafeValueAt( negativeSize )

        val distance1 = operations.getDistance( valueZero, valueAtNegative )
        val distance2 = operations.getDistance( valueAtNegative, valueZero )

        assertEquals( positiveSize, distance1 )
        assertEquals( positiveSize, distance2 )
    }

    @Test
    fun unsafeValueAt_is_always_positive()
    {
        val valueAtPositive = operations.unsafeValueAt( positiveSize )
        assertTrue( valueAtPositive > valueZero, "$valueAtPositive isn't > $valueZero." )

        if ( sizeOperations.isSignedType )
        {
            val negativeSize = sizeOperations.unsafeSubtract( sizeZero, positiveSize )
            val valueAtNegative = operations.unsafeValueAt( negativeSize )
            assertTrue( valueAtNegative > valueZero, "$valueAtNegative isn't > $valueZero." )
        }
    }

    @Test
    fun unsafeShift_with_positive_value()
    {
        val shiftedRight = operations.unsafeShift( valueZero, positiveSize, invertDirection = false )

        val positiveValue = operations.unsafeValueAt( positiveSize )
        assertEquals( positiveValue, shiftedRight )

        val shiftedLeft = operations.unsafeShift( positiveValue, positiveSize, invertDirection = true )
        assertEquals( valueZero, shiftedLeft )
    }

    @Test
    fun unsafeShift_with_negative_value()
    {
        if ( !sizeOperations.isSignedType ) return

        val negativeSize = sizeOperations.unsafeSubtract( sizeZero, positiveSize )
        assertEquals(
            operations.unsafeShift( valueZero, positiveSize, invertDirection = true ),
            operations.unsafeShift( valueZero, negativeSize, invertDirection = false )
        )
        assertEquals(
            operations.unsafeShift( valueZero, positiveSize, invertDirection = false ),
            operations.unsafeShift( valueZero, negativeSize, invertDirection = true )
        )
    }

    @Test
    fun unsafeShift_zero()
    {
        val shiftedRight = operations.unsafeShift( valueZero, sizeZero, invertDirection = false )
        assertEquals( valueZero, shiftedRight )

        val shiftedLeft = operations.unsafeShift( valueZero, sizeZero, invertDirection = true )
        assertEquals( valueZero, shiftedLeft )
    }

    @Test
    fun unsafeShift_full_range()
    {
        val fullRange = operations.getDistance( operations.minValue, operations.maxValue )

        val toMax = operations.unsafeShift( operations.minValue, fullRange, invertDirection = false )
        assertEquals( operations.maxValue, toMax )

        val toMin = operations.unsafeShift( operations.maxValue, fullRange, invertDirection = true )
        assertEquals( operations.minValue, toMin )
    }

    @Test
    fun unsafeShift_can_overflow()
    {
        val max = valueOperations.maxValue
        val shiftMaxOverflow = operations.unsafeShift( valueOperations.maxValue, positiveSize, invertDirection = false )

        val min = valueOperations.minValue
        val shiftMinOverflow = operations.unsafeShift( valueOperations.minValue, positiveSize, invertDirection = true )

        // Types either overflow, or are coerced to max value (e.g. floating points).
        assertTrue( shiftMaxOverflow < max || shiftMaxOverflow == max )
        assertTrue( shiftMinOverflow > min || shiftMinOverflow == min )
    }

    @Test
    fun unsafeShift_with_coerced_max_size_values()
    {
        val maxShift = sizeOperations.maxValue
        val maxOverflow = sizeOperations.unsafeAdd( maxShift, maxShift )
        if ( maxShift == maxOverflow )
        {
            assertFailsWith<IllegalArgumentException> {
                operations.unsafeShift( valueZero, maxShift, invertDirection = false )
            }
        }

        val minShift = sizeOperations.minValue
        val minOverflow = sizeOperations.unsafeSubtract( minShift, maxShift )
        if ( minShift == minOverflow )
        {
            assertFailsWith<IllegalArgumentException> {
                operations.unsafeShift(valueZero, minShift, invertDirection = true)
            }
        }
    }
}
