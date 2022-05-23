package io.github.whathecode.kotlinx.interval

import kotlin.test.*


class Readme
{
    @Test
    fun introduction_example()
    {
        val interval = IntInterval(
            start = 0,
            isStartIncluded = true,
            end = 10,
            isEndIncluded = false
        )
        val areIncluded = 0 in interval && 5 in interval // true
        val areExcluded = 10 !in interval // true
        val size: UInt = interval.size // 10
    }
}
