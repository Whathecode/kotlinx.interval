# Kotlin Multiplatform Bounded Open/Closed Generic Intervals

[![Build and test](https://github.com/Whathecode/kotlinx.interval/actions/workflows/ci.yml/badge.svg)](https://github.com/Whathecode/kotlinx.interval/actions/workflows/ci.yml)

Represent closed, open, or half-open, bounded intervals in Kotlin and perform common operations on them.
_Values_ covered by the interval can be of a different type than _distances_ between those values.

For example, `IntInterval` has `Int` values and `UInt` distances:

```kotlin
val interval = IntInterval(
    start = 0,
    isStartIncluded = true,
    end = 10,
    isEndIncluded = false
)
val size: UInt = interval.size // 10
```

This protects against overflows (e.g. if `size > Int.MAX_VALUE`) but also offers better semantics.
For example, you can create intervals for [kotlinx datetime](https://github.com/Kotlin/kotlinx-datetime) `Instant` values which are a `Duration` apart.

## Interval Types

This library includes a generic base class `Interval<T, TSize>` which can be used to create intervals for any type.
To achieve this, it directs type operations to `IntervalTypeOperations` which the constructor takes as a parameter.

The following interval types are included by default:

|       Type       | Values (`T`) | Distances (`TSize`) |
|:----------------:|:------------:|:-------------------:|
|  `ByteInterval`  |    `Byte`    |       `UByte`       |
| `ShortInterval`  |   `Short`    |      `UShort`       |
|  `IntInterval`   |    `Int`     |       `UInt`        |
|  `LongInterval`  |    `Long`    |       `ULong`       |
| `FloatInterval`  |   `Float`    |      `Double`       |
| `UByteInterval`  |   `UByte`    |       `UByte`       |
| `UShortInterval` |   `UShort`   |      `UShort`       |
|  `UIntInterval`  |    `UInt`    |       `UInt`        |
| `ULongInterval`  |   `ULong`    |       `ULong`       |
|  `CharInterval`  |    `Char`    |      `UShort`       |