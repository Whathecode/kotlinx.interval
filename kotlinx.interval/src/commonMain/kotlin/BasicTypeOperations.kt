package io.github.whathecode.kotlinx.interval

import kotlin.math.roundToInt
import kotlin.math.roundToLong
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
    override val maxValue: Byte = Byte.MAX_VALUE
    override val spacing: Byte = 1

    override fun unsafeAdd( a: Byte, b: Byte ): Byte = (a + b).toByte()
    override fun unsafeSubtract( a: Byte, b: Byte ): Byte = (a - b).toByte()

    override fun fromDouble( double: Double ) = double.roundToInt().toByte()
    override fun toDouble( value: Byte ) = value.toDouble()
}

internal object ShortOperations : TypeOperations<Short>
{
    override val additiveIdentity: Short = 0
    override val minValue: Short = Short.MIN_VALUE
    override val maxValue: Short = Short.MAX_VALUE
    override val spacing: Short = 1

    override fun unsafeAdd( a: Short, b: Short ): Short = (a + b).toShort()
    override fun unsafeSubtract( a: Short, b: Short ): Short = (a - b).toShort()

    override fun fromDouble( double: Double ) = double.roundToInt().toShort()
    override fun toDouble( value: Short ) = value.toDouble()
}

internal object IntOperations : TypeOperations<Int>
{
    override val additiveIdentity: Int = 0
    override val minValue: Int = Int.MIN_VALUE
    override val maxValue: Int = Int.MAX_VALUE
    override val spacing: Int = 1

    override fun unsafeAdd( a: Int, b: Int ): Int = a + b
    override fun unsafeSubtract( a: Int, b: Int ): Int = a - b

    override fun fromDouble( double: Double ): Int = double.roundToInt()
    override fun toDouble( value: Int ): Double = value.toDouble()
}

internal object LongOperations : TypeOperations<Long>
{
    override val additiveIdentity: Long = 0
    override val minValue: Long = Long.MIN_VALUE
    override val maxValue: Long = Long.MAX_VALUE
    override val spacing: Long = 1

    override fun unsafeAdd( a: Long, b: Long ): Long = a + b
    override fun unsafeSubtract( a: Long, b: Long ): Long = a - b

    override fun fromDouble( double: Double ): Long = double.roundToLong()
    override fun toDouble( value: Long ): Double = value.toDouble()
}

internal object FloatOperations : TypeOperations<Float>
{
    override val additiveIdentity: Float = 0f
    override val minValue: Float = Float.NEGATIVE_INFINITY
    override val maxValue: Float = Float.POSITIVE_INFINITY
    override val spacing: Float? = null

    override fun unsafeAdd( a: Float, b: Float ): Float = a + b
    override fun unsafeSubtract( a: Float, b: Float ): Float = a - b

    override fun fromDouble( double: Double ): Float = double.toFloat()
    override fun toDouble( value: Float ): Double = value.toDouble()
}

internal object DoubleOperations : TypeOperations<Double>
{
    override val additiveIdentity: Double = 0.0
    override val minValue: Double = Double.NEGATIVE_INFINITY
    override val maxValue: Double = Double.POSITIVE_INFINITY
    override val spacing: Double? = null

    override fun unsafeAdd( a: Double, b: Double ): Double = a + b
    override fun unsafeSubtract( a: Double, b: Double ): Double = a - b

    override fun fromDouble( double: Double ): Double = double
    override fun toDouble( value: Double ): Double = value
}

internal object UByteOperations : TypeOperations<UByte>
{
    override val additiveIdentity: UByte = 0.toUByte()
    override val minValue: UByte = UByte.MIN_VALUE
    override val maxValue: UByte = UByte.MAX_VALUE
    override val spacing: UByte = 1.toUByte()

    override fun unsafeAdd( a: UByte, b: UByte ): UByte = (a + b).toUByte()
    override fun unsafeSubtract( a: UByte, b: UByte ): UByte = (a - b).toUByte()

    override fun fromDouble( double: Double ): UByte = double.roundToInt().toUByte()
    override fun toDouble( value: UByte ): Double = value.toDouble()
}

internal object UShortOperations : TypeOperations<UShort>
{
    override val additiveIdentity: UShort = 0.toUShort()
    override val minValue: UShort = UShort.MIN_VALUE
    override val maxValue: UShort = UShort.MAX_VALUE
    override val spacing: UShort? = 1.toUShort()

    override fun unsafeAdd( a: UShort, b: UShort ): UShort = (a + b).toUShort()
    override fun unsafeSubtract( a: UShort, b: UShort ): UShort = (a - b).toUShort()

    override fun fromDouble( double: Double ): UShort = double.roundToInt().toUShort()
    override fun toDouble( value: UShort ): Double = value.toDouble()
}

internal object UIntOperations : TypeOperations<UInt>
{
    override val additiveIdentity: UInt = 0.toUInt()
    override val minValue: UInt = UInt.MIN_VALUE
    override val maxValue: UInt = UInt.MAX_VALUE
    override val spacing: UInt? = 1.toUInt()

    override fun unsafeAdd( a: UInt, b: UInt ): UInt = a + b
    override fun unsafeSubtract( a: UInt, b: UInt ): UInt = a - b

    override fun fromDouble( double: Double ): UInt = double.toUInt()
    override fun toDouble( value: UInt ): Double = value.toDouble()
}

internal object ULongOperations : TypeOperations<ULong>
{
    override val additiveIdentity: ULong = 0.toULong()
    override val minValue: ULong = ULong.MIN_VALUE
    override val maxValue: ULong = ULong.MAX_VALUE
    override val spacing: ULong? = 1.toULong()

    override fun unsafeAdd( a: ULong, b: ULong ): ULong = a + b
    override fun unsafeSubtract( a: ULong, b: ULong ): ULong = a - b

    override fun fromDouble( double: Double ): ULong = double.toULong()
    override fun toDouble( value: ULong ): Double = value.toDouble()
}

internal object CharOperations : TypeOperations<Char>
{
    override val additiveIdentity: Char = Char( 0 )
    override val minValue: Char get() = Char.MIN_VALUE
    override val maxValue: Char get() = Char.MAX_VALUE
    override val spacing: Char? = Char( 1 )

    override fun unsafeAdd( a: Char, b: Char ): Char = a + b.code
    override fun unsafeSubtract( a: Char, b: Char ): Char = (a - b).toChar()

    override fun fromDouble( double: Double ): Char = double.roundToInt().toChar()
    override fun toDouble( value: Char ): Double = value.code.toDouble()
}
