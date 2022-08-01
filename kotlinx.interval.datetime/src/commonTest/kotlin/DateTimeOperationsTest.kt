package io.github.whathecode.kotlinx.interval.datetime

import io.github.whathecode.kotlinx.interval.test.TypeOperationsTest
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


object InstantOperationsTest : TypeOperationsTest<Instant>(
    InstantOperations,
    a = Instant.fromEpochSeconds( 10, 10 ),
    b = Instant.fromEpochSeconds( 5, 15 ),
    aMinusB = Instant.fromEpochSeconds( 4, 1_000_000_000 - 5 )
)


object DurationOperationsTest : TypeOperationsTest<Duration>( DurationOperations, 10.seconds, 5.seconds, 5.seconds )
