package com.github.whathecode.kotlinx.interval


/**
 * Create an [Interval] representing the set of all [T] values lying between a provided [start] and [end] value.
 * The interval can be closed, open, or half-open, as determined by [isStartIncluded] and [isEndIncluded].
 *
 * @throws UnsupportedOperationException if [typeOperations] needs to be specified since no default is supported.
 */
inline fun <reified T : Comparable<T>> createInterval(
    start: T,
    isStartIncluded: Boolean,
    end: T,
    isEndIncluded: Boolean,
    /**
     * Specify how to access predefined operators of type [T].
     * For basic Kotlin types, this parameter is initialized with a matching default.
     */
    typeOperations: TypeOperations<T> = getBasicTypeOperationsFor()
) = Interval( start, isStartIncluded, end, isEndIncluded, typeOperations )
