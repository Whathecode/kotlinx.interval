@file:Suppress( "UNUSED_VARIABLE" )

package io.github.whathecode.kotlinx.interval.datetime

import kotlinx.datetime.Clock
import kotlin.test.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds


class Readme
{
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
