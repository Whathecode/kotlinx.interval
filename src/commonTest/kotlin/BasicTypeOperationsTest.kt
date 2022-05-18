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


object ByteOperationsTest : TypeOperationsTest<Byte>( ByteOperations, 10, 5, 5 )
object ShortOperationsTest : TypeOperationsTest<Short>( ShortOperations, 10, 5, 5 )
object IntOperationsTest : TypeOperationsTest<Int>( IntOperations, 10, 5, 5 )
object LongOperationsTest : TypeOperationsTest<Long>( LongOperations, 10, 5, 5 )
object FloatOperationsTest : TypeOperationsTest<Float>( FloatOperations, 10f, 5f, 5f )
object DoubleOperationsTest : TypeOperationsTest<Double>( DoubleOperations, 10.0, 5.0, 5.0 )
object UByteOperationsTest : TypeOperationsTest<UByte>( UByteOperations, 10.toUByte(), 5.toUByte(), 5.toUByte() )
object UShortOperationsTest : TypeOperationsTest<UShort>( UShortOperations, 10.toUShort(), 5.toUShort(), 5.toUShort() )
object UIntOperationsTest : TypeOperationsTest<UInt>( UIntOperations, 10.toUInt(), 5.toUInt(), 5.toUInt() )
object ULongOperationsTest : TypeOperationsTest<ULong>( ULongOperations, 10.toULong(), 5.toULong(), 5.toULong() )
object CharOperationsTest : TypeOperationsTest<Char>( CharOperations, 'b', 'a', 1.toChar() )
