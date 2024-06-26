package io.github.whathecode.kotlinx.interval


/**
 * Provides generic access to the predefined set of type operators of type [T].
 */
interface TypeOperations<T : Comparable<T>>
{
    /**
     * When this value is added to any value x, it yields x. I.e. commonly "zero".
     */
    val additiveIdentity: T

    /**
     * The minimum value an instance of [T] can have.
     */
    val minValue: T

    /**
     * The maximum value an instance of [T] can have.
     */
    val maxValue: T

    /**
     * If values of [T] are evenly spaced, the distance between each subsequent value of [T];
     * null otherwise, e.g., if values of [T] are dense.
     */
    val spacing: T?

    /**
     * The binary operation `a + b`, not safeguarded against overflows.
     * The resulting type is cast back to type [T].
     */
    fun unsafeAdd( a: T, b: T ): T

    /**
     * The binary operation `a - b`, not safeguarded against overflows.
     * The resulting type is cast back to type [T].
     */
    fun unsafeSubtract( a: T, b: T ): T
}
