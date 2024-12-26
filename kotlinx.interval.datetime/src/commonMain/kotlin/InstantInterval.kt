package io.github.whathecode.kotlinx.interval.datetime

import io.github.whathecode.kotlinx.interval.Interval
import io.github.whathecode.kotlinx.interval.IntervalTypeOperations
import kotlinx.datetime.Instant
import kotlin.math.absoluteValue
import kotlin.time.Duration


/**
 * An [Interval] representing the set of all [Instant] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class InstantInterval( start: Instant, isStartIncluded: Boolean, end: Instant, isEndIncluded: Boolean )
    : Interval<Instant, Duration>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = object : IntervalTypeOperations<Instant, Duration>(
            InstantOperations,
            DurationOperations,
            getDistanceTo = { (InstantOperations.additiveIdentity - it).absoluteValue },
            unsafeValueAt = { InstantOperations.additiveIdentity + it.absoluteValue }
        )
        {
            // Maximum positive/negative value to ensure the interval size can be represented by Duration.
            // One is subtracted to exclude `Duration.Infinity`.
            private val MAX = (DurationOperations.MAX_MILLIS - 1) / 2

            // Some platforms have a smaller range than `MAX` duration, and values are clamped on initialization.
            private val COERCED_MAX_SECONDS = minOf(
                Instant.fromEpochMilliseconds( -MAX ).epochSeconds.absoluteValue,
                Instant.fromEpochMilliseconds( MAX ).epochSeconds.absoluteValue
            )

            override val minValue: Instant = Instant.fromEpochSeconds( -COERCED_MAX_SECONDS, 0 )
            override val maxValue: Instant = Instant.fromEpochSeconds( COERCED_MAX_SECONDS, 0 )
        }
    }
}

/**
 * Create an [InstantInterval] representing the set of all [Instant] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: Instant, end: Instant, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    InstantInterval( start, isStartIncluded, end, isEndIncluded )
