@file:Suppress( "UNUSED_VARIABLE" )

package io.github.whathecode.kotlinx.interval

import kotlin.test.*
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds


class Readme
{
    @Test
    fun introduction_int_interval_example()
    {
        val interval: IntInterval = interval( 0, 10, isEndIncluded = false )
        val areIncluded = 0 in interval && 5 in interval // true
        val areExcluded = 10 !in interval && 15 !in interval // true
        val size: UInt = interval.size // 10
        val shifted = interval shr 10u // Shifted right by 10: [10, 20)
    }

    @Test
    fun introduction_chaining_operations_example()
    {
        val start = interval( 0, 100 ) // Interval: [0, 100]
        val areIncluded = 50 in start && 100 in start // true
        val splitInTwo = start - interval( 25, 85 ) // Union: [[0, 25), (85, 100]]
        val shiftBackAndForth = splitInTwo shr 100u shl 100u // == splitInTwo
        val areExcluded = 50 !in splitInTwo && 85 !in splitInTwo // true
        val unite = splitInTwo + interval( 10, 90 ) // Interval: [0, 100]
        val backToStart = start == unite // true
    }

    @Test
    fun introduction_instant_interval_example()
    {
        val now = Clock.System.now()
        val interval: InstantInterval = interval( now, now + 100.seconds )
        val areIncluded = now + 50.seconds in interval // true
        val size: Duration = interval.size // 100 seconds
        val shifted = interval shr 24.hours // 100 seconds 24 hours from now
    }
}
