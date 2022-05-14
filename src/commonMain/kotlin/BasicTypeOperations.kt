package com.github.whathecode.kotlinx.interval

import kotlin.reflect.KClass


/**
 * For each of Kotlin's basic types, [TypeOperations] which provide generic access to the predefined set of operators.
 */
val basicTypeOperations: Map<KClass<*>, TypeOperations<*>> = mapOf(
    Byte::class to ByteOperations,
    Short::class to ShortOperations,
    Int::class to IntOperations,
    Long::class to LongOperations,
    Float::class to FloatOperations,
    Double::class to DoubleOperations,
    UByte::class to UByteOperations,
    UShort::class to UShortOperations,
    UInt::class to UIntOperations,
    ULong::class to ULongOperations,
    Char::class to CharOperations
)

/**
 * Get [TypeOperations] for the basic Kotlin type [T].
 *
 * @throws UnsupportedOperationException if no [TypeOperations] is available for [T].
 */
inline fun <reified T> getBasicTypeOperationsFor(): TypeOperations<T>
{
    val klass = T::class
    val retrievedOperations = basicTypeOperations[ klass ] ?:
        throw UnsupportedOperationException( "No `${TypeOperations::class.simpleName}` available for type `$klass`." )

    @Suppress( "UNCHECKED_CAST" )
    return retrievedOperations as TypeOperations<T>
}


private object ByteOperations : TypeOperations<Byte>
{
    override fun unsafeSubtract( a: Byte, b: Byte ): Byte = (a - b).toByte()
}

private object ShortOperations : TypeOperations<Short>
{
    override fun unsafeSubtract( a: Short, b: Short ): Short = (a - b).toShort()
}

private object IntOperations : TypeOperations<Int>
{
    override fun unsafeSubtract( a: Int, b: Int ): Int = a - b
}

private object LongOperations : TypeOperations<Long>
{
    override fun unsafeSubtract( a: Long, b: Long ): Long = a - b
}

private object FloatOperations : TypeOperations<Float>
{
    override fun unsafeSubtract( a: Float, b: Float ): Float = a - b
}

private object DoubleOperations : TypeOperations<Double>
{
    override fun unsafeSubtract( a: Double, b: Double ): Double = a - b
}

private object UByteOperations : TypeOperations<UByte>
{
    override fun unsafeSubtract( a: UByte, b: UByte ): UByte = (a - b).toUByte()
}

private object UShortOperations : TypeOperations<UShort>
{
    override fun unsafeSubtract( a: UShort, b: UShort ): UShort = (a - b).toUShort()
}

private object UIntOperations : TypeOperations<UInt>
{
    override fun unsafeSubtract( a: UInt, b: UInt ): UInt = a - b
}

private object ULongOperations : TypeOperations<ULong>
{
    override fun unsafeSubtract(a: ULong, b: ULong ): ULong = a - b
}

private object CharOperations : TypeOperations<Char>
{
    override fun unsafeSubtract( a: Char, b: Char ): Char = (a - b).toChar()
}
