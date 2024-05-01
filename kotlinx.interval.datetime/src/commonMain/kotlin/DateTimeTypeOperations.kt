package io.github.whathecode.kotlinx.interval.datetime

import io.github.whathecode.kotlinx.interval.TypeOperations
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


internal object InstantOperations : TypeOperations<Instant>
{
    override val additiveIdentity: Instant = Instant.fromEpochMilliseconds( 0 )
    override val minValue: Instant = Instant.fromEpochSeconds( Long.MIN_VALUE, Long.MIN_VALUE )
    override val maxValue: Instant = Instant.fromEpochSeconds( Long.MAX_VALUE, Long.MAX_VALUE )
    override val spacing: Instant? = null

    override fun unsafeAdd( a: Instant, b: Instant ): Instant = Instant.fromEpochSeconds(
        a.epochSeconds + b.epochSeconds,
        a.nanosecondsOfSecond + b.nanosecondsOfSecond
    )

    override fun unsafeSubtract( a: Instant, b: Instant ): Instant = Instant.fromEpochSeconds(
        a.epochSeconds - b.epochSeconds,
        a.nanosecondsOfSecond - b.nanosecondsOfSecond
    )
}


internal object DurationOperations : TypeOperations<Duration>
{
    override val additiveIdentity: Duration = Duration.ZERO

    private const val MAX_MILLIS = Long.MAX_VALUE / 2
    override val minValue: Duration = -MAX_MILLIS.milliseconds
    override val maxValue: Duration = MAX_MILLIS.milliseconds
    override val spacing: Duration? = null

    override fun unsafeAdd( a: Duration, b: Duration ): Duration = a + b
    override fun unsafeSubtract( a: Duration, b: Duration ): Duration = a - b
}
