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
}
