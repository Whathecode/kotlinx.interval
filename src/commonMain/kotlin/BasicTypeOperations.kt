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
inline fun <reified T : Comparable<T>> getBasicTypeOperationsFor(): TypeOperations<T>
{
    val klass = T::class
    val retrievedOperations = basicTypeOperations[ klass ] ?:
        throw UnsupportedOperationException( "No `${TypeOperations::class.simpleName}` available for type `$klass`." )

    @Suppress( "UNCHECKED_CAST" )
    return retrievedOperations as TypeOperations<T>
}


internal object ByteOperations : TypeOperations<Byte>
{
    override val additiveIdentity: Byte = 0
    override val minValue: Byte = Byte.MIN_VALUE

    override fun unsafeSubtract( a: Byte, b: Byte ): Byte = (a - b).toByte()
}

internal object ShortOperations : TypeOperations<Short>
{
    override val additiveIdentity: Short = 0
    override val minValue: Short = Short.MIN_VALUE

    override fun unsafeSubtract( a: Short, b: Short ): Short = (a - b).toShort()
}

internal object IntOperations : TypeOperations<Int>
{
    override val additiveIdentity: Int = 0
    override val minValue: Int = Int.MIN_VALUE

    override fun unsafeSubtract( a: Int, b: Int ): Int = a - b
}

internal object LongOperations : TypeOperations<Long>
{
    override val additiveIdentity: Long = 0
    override val minValue: Long = Long.MIN_VALUE

    override fun unsafeSubtract( a: Long, b: Long ): Long = a - b
}

internal object FloatOperations : TypeOperations<Float>
{
    override val additiveIdentity: Float = 0f
    override val minValue: Float = -Float.MAX_VALUE

    override fun unsafeSubtract( a: Float, b: Float ): Float = a - b
}

internal object DoubleOperations : TypeOperations<Double>
{
    override val additiveIdentity: Double = 0.0
    override val minValue: Double = -Double.MAX_VALUE

    override fun unsafeSubtract( a: Double, b: Double ): Double = a - b
}

internal object UByteOperations : TypeOperations<UByte>
{
    override val additiveIdentity: UByte = 0.toUByte()
    override val minValue: UByte = UByte.MIN_VALUE

    override fun unsafeSubtract( a: UByte, b: UByte ): UByte = (a - b).toUByte()
}

internal object UShortOperations : TypeOperations<UShort>
{
    override val additiveIdentity: UShort = 0.toUShort()
    override val minValue: UShort = UShort.MIN_VALUE

    override fun unsafeSubtract( a: UShort, b: UShort ): UShort = (a - b).toUShort()
}

internal object UIntOperations : TypeOperations<UInt>
{
    override val additiveIdentity: UInt = 0.toUInt()
    override val minValue: UInt = UInt.MIN_VALUE

    override fun unsafeSubtract( a: UInt, b: UInt ): UInt = a - b
}

internal object ULongOperations : TypeOperations<ULong>
{
    override val additiveIdentity: ULong = 0.toULong()
    override val minValue: ULong = ULong.MIN_VALUE

    override fun unsafeSubtract( a: ULong, b: ULong ): ULong = a - b
}

internal object CharOperations : TypeOperations<Char>
{
    // HACK: a getter is added to work around a JS legacy boxing/unboxing bug.
    override val additiveIdentity: Char get() = Char( 0 )
    override val minValue: Char get() = Char.MIN_VALUE

    override fun unsafeSubtract( a: Char, b: Char ): Char = (a - b).toChar()
}
