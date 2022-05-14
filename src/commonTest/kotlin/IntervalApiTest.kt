package com.github.whathecode.kotlinx.interval

import kotlin.test.*


class IntervalApiTest
{
    @Test
    fun createInterval_succeeds_for_default_types()
    {
        val interval = createInterval( 0, true, 10, true )

        assertEquals( 0, interval.start )
        assertTrue( interval.isStartIncluded )
        assertEquals( 10, interval.end )
        assertTrue( interval.isEndIncluded )
    }

    @Test
    fun createInterval_fails_for_unknown_type()
    {
        class Unknown : Comparable<Unknown>
        {
            override fun compareTo( other: Unknown ): Int = 0
        }

        assertFailsWith<UnsupportedOperationException> { createInterval( Unknown(), true, Unknown(), true ) }
    }
}
