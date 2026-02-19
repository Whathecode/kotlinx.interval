package io.github.whathecode.kotlinx.interval

import io.github.whathecode.kotlinx.interval.test.IntervalTest
import kotlin.time.Duration
import kotlin.time.Instant


private val a = Instant.fromEpochSeconds( 0, 50 )
private val b = Instant.fromEpochSeconds( 0, 100 )
private val c = Instant.fromEpochSeconds( 0, 150 )

object InstantIntervalTest : IntervalTest<Instant, Duration>( a, b, c, b - a, InstantInterval.Operations )
