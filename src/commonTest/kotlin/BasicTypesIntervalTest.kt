package com.github.whathecode.kotlinx.interval


class ByteIntervalTest : IntervalTest<Byte>( 0, 5, 10 )
class ShortIntervalTest : IntervalTest<Short>( 0, 5, 10 )
class IntIntervalTest : IntervalTest<Int>( 0, 5, 10 )
class LongIntervalTest : IntervalTest<Long>( 0, 5, 10 )
class FloatIntervalTest : IntervalTest<Float>( 0f, 5f, 10f )
class DoubleIntervalTest : IntervalTest<Double>( 0.0, 5.0, 10.0 )

class UByteIntervalTest : IntervalTest<UByte>( 0.toUByte(), 5.toUByte(), 10.toUByte() )
class UShortIntervalTest : IntervalTest<UShort>( 0.toUShort(), 5.toUShort(), 10.toUShort() )
class UIntIntervalTest : IntervalTest<UInt>( 0.toUInt(), 5.toUInt(), 10.toUInt() )
class ULongIntervalTest : IntervalTest<ULong>( 0.toULong(), 5.toULong(), 10.toULong() )

class CharIntervalTest : IntervalTest<Char>( 'a', 'b', 'c' )
