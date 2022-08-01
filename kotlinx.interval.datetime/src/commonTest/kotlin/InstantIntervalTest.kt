package io.github.whathecode.kotlinx.interval.datetime

import io.github.whathecode.kotlinx.interval.test.IntervalTest
import kotlinx.datetime.Instant
import kotlin.time.Duration


private val a = Instant.fromEpochSeconds( 0, 50 )
private val b = Instant.fromEpochSeconds( 0, 100 )
private val c = Instant.fromEpochSeconds( 100, 50 )

object InstantIntervalTest : IntervalTest<Instant, Duration>( a, b, c, b - a, InstantInterval.Operations )
