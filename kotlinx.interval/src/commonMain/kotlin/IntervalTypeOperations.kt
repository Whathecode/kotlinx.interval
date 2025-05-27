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
     * Return the distance between two values of [T].
     */
    val getDistance: (T, T) -> TSize,
    /**
     * Returns the positive value [T] at the specified distance from the additive identity (usually "zero") of [T].
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

    
    /**
     * Returns a new value offset by the specified [amount] from the given [value].
     * This isn't safeguarded against overflows in case the resulting value goes out of bounds of [T].
     *
     * @param invertDirection Inverts the direction by which the value is shifted. I.e., if [amount] is positive,
     *   shift left instead of shift right, and vice verse. This can be used to shift values of unsigned types,
     *   which can't represent a negative [amount], left.
     * @throws IllegalArgumentException if [amount] represents an infinite value; no meaningful value can be returned.
     */
    fun unsafeShift( value: T, amount: TSize, invertDirection: Boolean ): T
    {
        val sizeZero = sizeOperations.additiveIdentity
        val minSize =
            if ( sizeOperations.isSignedType )
            {
                sizeOperations.unsafeSubtract( sizeZero, getDistance( valueOperations.additiveIdentity, minValue ) )
            }
            else { sizeOperations.additiveIdentity }
        val maxSize = getDistance( valueOperations.additiveIdentity, maxValue )

        var toShift = amount
        var curValue = value
        while ( true )
        {
            val prevShift = toShift

            if ( toShift > maxSize )
            {
                toShift = sizeOperations.unsafeSubtract( toShift, maxSize )
                curValue =
                    if ( invertDirection ) valueOperations.unsafeSubtract( curValue, maxValue )
                    else valueOperations.unsafeAdd( curValue, maxValue )
            }
            else if ( toShift < minSize )
            {
                toShift = sizeOperations.unsafeSubtract( toShift, minSize )
                curValue =
                    if ( invertDirection ) valueOperations.unsafeSubtract( curValue, minValue )
                    else valueOperations.unsafeAdd( curValue, minValue )
            }
            else
            {
                val shiftRight = if ( toShift >= sizeZero ) !invertDirection else invertDirection
                val shiftAbs = unsafeValueAt( toShift )
                curValue =
                    if ( shiftRight ) valueOperations.unsafeAdd( curValue, shiftAbs )
                    else valueOperations.unsafeSubtract( curValue, shiftAbs )
                break;
            }

            // If the amount to shift isn't reduced after one iteration, it must be coerced to an "infinity" value.
            // This makes this an infinite operation with no meaningful output.
            require( toShift != prevShift ) { "The passed amount to shift is not allowed to be infinite." }
        }

        return curValue
    }
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
     * A function returning the distance between values of [T].
     */
    noinline getDistance: (T, T) -> TSize,
    /**
     * A function returning the value [T] at the specified distance from the additive identity (usually "zero") of [T],
     * not safeguarded against overflows in case the value is larger than the maximum value of [T].
     */
    noinline unsafeValueAt: (TSize) -> T
) = object : IntervalTypeOperations<T, TSize>( valueOperations, sizeOperations, getDistance, unsafeValueAt )
{
    override val minValue: T = valueOperations.minValue
    override val maxValue: T = valueOperations.maxValue
}
