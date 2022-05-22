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
        val size: UInt = interval.size // 10
    }
}
