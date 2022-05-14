package com.github.whathecode.kotlinx.interval

import kotlin.test.*


class BasicTypeOperationsTest
{
    @Test
    fun getBasicTypeOperationsFor_succeeds_for_basic_types()
    {
        getBasicTypeOperationsFor<Int>()
    }

    @Test
    fun getBasicTypeOperationsFor_fails_for_unknown_types()
    {
        class Unknown

        assertFailsWith<UnsupportedOperationException> { getBasicTypeOperationsFor<Unknown>() }
    }
}
