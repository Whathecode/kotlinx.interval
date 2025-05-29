@file:Suppress( "UNUSED_VARIABLE" )

package io.github.whathecode.kotlinx.interval.datetime

import io.github.whathecode.kotlinx.interval.IntInterval
import io.github.whathecode.kotlinx.interval.interval
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
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

    @Test
    fun introduction_common_math()
    {
        // Two intervals of different types.
        val start2025 = LocalDateTime( 2025, 1, 1, 0, 0 ).toInstant( TimeZone.UTC )
        val end2025 = LocalDateTime( 2026, 1, 1, 0, 0 ).toInstant( TimeZone.UTC )
        val year2025: InstantInterval = interval( start2025, end2025 )
        val timelineUi: IntInterval = interval( 0, 800 ) // UI element 800 pixels wide

        // Find the selected time at a given UI coordinate using linear interpolation.
        val mouseX = 400
        val uiPercentage: Double = timelineUi.getPercentageFor( mouseX )
        val selectedTime: Instant = year2025.getValueAt( uiPercentage ) // July 2nd at noon.
    }
}
