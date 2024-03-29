# Kotlin Multiplatform Bounded Open/Closed Generic Intervals

[![Publish snapshots](https://github.com/Whathecode/kotlinx.interval/actions/workflows/publish-snapshots.yml/badge.svg)](https://github.com/Whathecode/kotlinx.interval/actions/workflows/ci.yml)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.whathecode.kotlinx.interval/kotlinx-interval?server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/whathecode/kotlinx/interval)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.whathecode.kotlinx.interval/kotlinx-interval/badge.svg?color=orange)](https://mvnrepository.com/artifact/io.github.whathecode.kotlinx.interval)

Represent closed, open, or half-open, bounded intervals in Kotlin and perform common operations on them.
_Values_ covered by the interval can be of a different type than _distances_ between those values.

For example, `IntInterval` has `Int` values and `UInt` distances:

```kotlin
val interval: IntInterval = interval( 0, 10, isEndIncluded = false )
val areIncluded = 0 in interval && 5 in interval // true
val areExcluded = 10 !in interval && 15 !in interval // true
val size: UInt = interval.size // 10
```

This protects against overflows (e.g. if `size > Int.MAX_VALUE`) but also offers better semantics.
For example, this library supports [kotlinx datetime](https://github.com/Kotlin/kotlinx-datetime) `Instant` values which are a `Duration` apart.

```kotlin
val now = Clock.System.now()
val interval: InstantInterval = interval( now, now + 100.seconds )
val areIncluded = now + 50.seconds in interval // true
val size: Duration = interval.size // 100 seconds
```

## Interval Types

This library includes a generic base class `Interval<T, TSize>` which can be used to create intervals for any type.
To achieve this, it directs type operations to `IntervalTypeOperations` which the constructor takes as a parameter.

The following interval types are included in `io.github.whathecode.kotlinx.interval:kotlinx-interval` on Maven:

|       Type       | Values (`T`) | Distances (`TSize`) |
|:----------------:|:------------:|:-------------------:|
|  `ByteInterval`  |    `Byte`    |       `UByte`       |
| `ShortInterval`  |   `Short`    |      `UShort`       |
|  `IntInterval`   |    `Int`     |       `UInt`        |
|  `LongInterval`  |    `Long`    |       `ULong`       |
| `FloatInterval`  |   `Float`    |      `Double`       |
| `DoubleInterval` |   `Double`   |      `Double`       |
| `UByteInterval`  |   `UByte`    |       `UByte`       |
| `UShortInterval` |   `UShort`   |      `UShort`       |
|  `UIntInterval`  |    `UInt`    |       `UInt`        |
| `ULongInterval`  |   `ULong`    |       `ULong`       |
|  `CharInterval`  |    `Char`    |      `UShort`       |

### Date/time intervals
Date/time intervals are implemented as `InstantInterval` using the [kotlinx datetime](https://github.com/Kotlin/kotlinx-datetime) library.
Since you may not always want to pull in this dependency, this class is published separately in `io.github.whathecode.kotlinx.interval:kotlinx-interval-datetime`.
