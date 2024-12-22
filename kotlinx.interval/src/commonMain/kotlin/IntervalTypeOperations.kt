package io.github.whathecode.kotlinx.interval


/**
 * Provides generic access to the predefined set of type operators of interval values of type [T] ([valueOperations])
 * and distances between values of type [TSize] ([sizeOperations]), and operations to convert between them.
 */
abstract class IntervalTypeOperations<T : Comparable<T>, TSize : Comparable<TSize>>(
    /**
     * Provide access to the predefined set of operators of [T].
     */
    val valueOperations: TypeOperations<T>,
    /**
     * Provide access to the predefined set of operators of [TSize].
     */
    val sizeOperations: TypeOperations<TSize>,
    /**
     * Return the distance from a specified value [T] to the additive identity (usually "zero") of [T].
     */
    val getDistanceTo: (T) -> TSize,
    /**
     * Returns the value [T] at the specified distance from the additive identity (usually "zero") of [T].
     * This isn't safeguarded against overflows in case the value is larger than the maximum value of [T].
     */
    val unsafeValueAt: (TSize) -> T
)
{
    /**
     * The minimum allowed value of [T] to ensure that the interval size can still be represented by [TSize].
     */
    abstract val minValue: T

    /**
     * The maximum allowed value of [T] to ensure that the interval size can still be represented by [TSize].
     */
    abstract val maxValue: T
}


/**
 * Create [IntervalTypeOperations] for intervals with values of type [T] and distances between values of type [TSize].
 *
 * @throws UnsupportedOperationException if [valueOperations] or [sizeOperations] needs to be specified
 * since no default is supported.
 */
inline fun <reified T : Comparable<T>, reified TSize : Comparable<TSize>> createIntervalTypeOperations(
    /**
     * Specify how to access predefined operators of type [T].
     * For basic Kotlin types, this parameter is initialized with a matching default.
     */
    valueOperations: TypeOperations<T> = getBasicTypeOperationsFor(),
    /**
     * Specify how to access predefined operators of type [TSize].
     * For basic Kotlin types, this parameter is initialized with a matching default.
     */
    sizeOperations: TypeOperations<TSize> = getBasicTypeOperationsFor(),
    /**
     * A function returning the distance from a specified value [T] to the additive identity (usually "zero") of [T].
     */
    noinline getDistanceTo: (T) -> TSize,
    /**
     * A function returning the value [T] at the specified distance from the additive identity (usually "zero") of [T],
     * not safeguarded against overflows in case the value is larger than the maximum value of [T].
     */
    noinline unsafeValueAt: (TSize) -> T
) = object : IntervalTypeOperations<T, TSize>( valueOperations, sizeOperations, getDistanceTo, unsafeValueAt )
{
    override val minValue: T = valueOperations.minValue
    override val maxValue: T = valueOperations.maxValue
}
