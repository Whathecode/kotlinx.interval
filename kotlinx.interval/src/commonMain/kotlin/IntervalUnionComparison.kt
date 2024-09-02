package io.github.whathecode.kotlinx.interval


/**
 * The result of comparing how two non-empty [IntervalUnion]'s are positioned in relation to one another.
 */
class IntervalUnionComparison<
    T : Comparable<T>,
    TSize : Comparable<TSize>,
    TUnion : IntervalUnion<T, TSize>
> private constructor(
    /**
     * The union with the smallest starting value out of the two unions,
     * or if equal, the first union passed to the comparison.
     */
    val lower: TUnion,
    /**
     * The bounds of [lower].
     */
    val lowerBounds: Interval<T, TSize>,
    /**
     * The union with the largest starting value out of the two unions,
     * or if equals, the second union passed to the comparison.
     */
    val upper: TUnion,
    /**
     * The bounds of [upper].
     */
    val upperBounds: Interval<T, TSize>,
    /**
     * Determines whether [lower] precedes [upper] with at least some values of [T] lying in between.
     */
    val isSplitPair: Boolean,
    /**
     * Determines whether [lowerBounds] fully encompasses [upperBounds].
     * I.e., if [lower] and [upper] are [Interval]'s, all values of [upper] are also contained in [lower].
     */
    val isFullyEncompassed: Boolean
)
{
    companion object
    {
        /**
         * Compare how [union1] and [union2] are positioned in relation to one another.
         * Both need to be non-empty.
         *
         * @throws IllegalArgumentException if one of the two unions is empty.
         */
        fun <T : Comparable<T>, TSize : Comparable<TSize>, TUnion : IntervalUnion<T, TSize>> of(
            union1: TUnion,
            union2: TUnion
        ): IntervalUnionComparison<T, TSize, TUnion>
        {
            // Start with assuming `union1` starts before `union2`.
            var lowerBounds = requireNotNull( union1.getBounds() ) { "`union1` shouldn't be empty." }
            var upperBounds = requireNotNull( union2.getBounds() ) { "`union2` shouldn't be empty." }
            var lower: TUnion = union1
            var upper: TUnion = union2

            // Swap if `union2` starts before.
            val swapCompare = upperBounds.start.compareTo( lowerBounds.start )
            if ( swapCompare < 0 || (swapCompare == 0 && upperBounds.isStartIncluded && !lowerBounds.isStartIncluded) )
            {
                lowerBounds = upperBounds.also { upperBounds = lowerBounds }
                lower = upper.also { upper = lower }
            }

            // Determine whether any values lie between both intervals.
            val operations = lowerBounds.valueOperations
            val gap = upperBounds.start.compareTo( lowerBounds.end )
            val isSplit =
                // Shared endpoints which neither interval includes means there is a gap.
                if ( gap == 0 && !(lowerBounds.isEndIncluded || upperBounds.isStartIncluded) ) true
                // For non-evenly spaced types (real numbers), any gap is sufficient.
                else if ( operations.spacing == null ) gap > 0
                // For evenly spaced types, the gap needs to be larger than the space between subsequent values.
                else gap > 0 && operations.unsafeSubtract( upperBounds.start, lowerBounds.end ) > operations.spacing!!

            // Determine whether upper is fully encompassed by lower.
            val endCompare = upperBounds.end.compareTo( lowerBounds.end )
            val isFullyEncompassed = endCompare < 0 ||
                (endCompare == 0 && !upperBounds.isEndIncluded && lowerBounds.isEndIncluded)

            return IntervalUnionComparison( lower, lowerBounds, upper, upperBounds, isSplit, isFullyEncompassed )
        }
    }
}
