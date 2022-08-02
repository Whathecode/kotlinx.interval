package io.github.whathecode.kotlinx.interval.datetime

import io.github.whathecode.kotlinx.interval.Interval
import io.github.whathecode.kotlinx.interval.IntervalTypeOperations
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds


/**
 * An [Interval] representing the set of all [Instant] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 */
class InstantInterval( start: Instant, isStartIncluded: Boolean, end: Instant, isEndIncluded: Boolean )
    : Interval<Instant, Duration>( start, isStartIncluded, end, isEndIncluded, Operations )
{
    companion object
    {
        internal val Operations = IntervalTypeOperations( InstantOperations, DurationOperations )
            { time: Instant -> (time.epochSeconds.seconds + time.nanosecondsOfSecond.nanoseconds).absoluteValue }
    }
}

/**
 * Create an [InstantInterval] representing the set of all [Instant] values lying between [start] and [end].
 * To exclude endpoints, set [isStartIncluded] or [isEndIncluded] to false; a closed interval is created by default.
 */
fun interval( start: Instant, end: Instant, isStartIncluded: Boolean = true, isEndIncluded: Boolean = true ) =
    InstantInterval( start, isStartIncluded, end, isEndIncluded )
