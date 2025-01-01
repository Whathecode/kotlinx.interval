package io.github.whathecode.kotlinx.interval.test

import io.github.whathecode.kotlinx.interval.IntervalTypeOperations
import kotlin.test.*


/**
 * Test for [IntervalTypeOperations].
 */
@Suppress( "FunctionName" )
open class IntervalTypeOperationsTest<T : Comparable<T>, TSize : Comparable<TSize>>(
    private val operations: IntervalTypeOperations<T, TSize>
)
{
    private val valueOperations = operations.valueOperations
    private val sizeOperations = operations.sizeOperations


    @Test
    fun minValue_can_be_converted_back_and_forth_using_TSize()
    {
        val min: T = operations.minValue
        val minSize: TSize = operations.getDistanceTo( min )
        val minSizeValue: T = operations.unsafeValueAt( minSize )
        val subtractedMinSize: T = valueOperations.unsafeSubtract( valueOperations.additiveIdentity, minSizeValue )
        assertEquals( min, subtractedMinSize )
    }

    @Test
    fun maxValue_can_be_converted_back_and_forth_using_TSize()
    {
        val max: T = operations.maxValue
        val maxSize: TSize = operations.getDistanceTo( max )
        val maxSizeValue: T = operations.unsafeValueAt( maxSize )
        assertEquals( max, maxSizeValue )
    }
}
