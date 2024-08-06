@file:Suppress( "UNUSED_VARIABLE" )

package io.github.whathecode.kotlinx.interval

import kotlin.test.*


class Readme
{
    @Test
    fun introduction_int_interval_example()
    {
        val interval: IntInterval = interval( 0, 10, isEndIncluded = false )
        val areIncluded = 0 in interval && 5 in interval // true
        val areExcluded = 10 !in interval && 15 !in interval // true
        val size: UInt = interval.size // 10
    }

    @Test
    fun introduction_chaining_operations_example()
    {
        val start = interval( 0, 100 ) // Interval: [0, 100]
        val areIncluded = 50 in start && 100 in start // true
        val splitInTwo = start - interval( 25, 85 ) // Union: [[0, 25), (85, 100]]
        val areExcluded = 50 !in splitInTwo && 85 !in splitInTwo // true
        val unite = splitInTwo + interval( 10, 90 ) // Interval: [0, 100]
        val backToStart = start == unite // true
    }
}
