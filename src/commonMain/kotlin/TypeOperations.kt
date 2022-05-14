package com.github.whathecode.kotlinx.interval


/**
 * Provides generic access to the predefined set of type operators of type [T].
 */
interface TypeOperations<T>
{
    /**
     * The binary operation `a - b`, not safeguarded against overflows.
     * The resulting type is cast back to type [T].
     */
    fun unsafeSubtract( a: T, b: T ): T
}
