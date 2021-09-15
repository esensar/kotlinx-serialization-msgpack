# kotlinx-serialization-msgpack-timestamp-extension

[![javadoc](https://javadoc.io/badge2/com.ensarsarajcic.kotlinx/serialization-msgpack-timestamp-extension/javadoc.svg)](https://javadoc.io/doc/com.ensarsarajcic.kotlinx/serialization-msgpack-timestamp-extension)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.ensarsarajcic.kotlinx/serialization-msgpack-timestamp-extension/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.ensarsarajcic.kotlinx/serialization-msgpack-timestamp-extension)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.ensarsarajcic.kotlinx/serialization-msgpack-timestamp-extension?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/com/ensarsarajcic/kotlinx/serialization-msgpack-timestamp-extension/)

## About

This module provides additional timestamp support. **Timestamp support is available without this module**, but this module provides specific serializers for `Instant` class from [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) library.

## Integration

### Maven central
**Gradle:**
```kotlin
implementation("com.ensarsarajcic.kotlinx:serialization-msgpack-timestamp-extension:${kotlinxSerializationMsgPackVersion}")
```

### Snapshot builds
**Gradle:**
```kotlin
repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
implementation("com.ensarsarajcic.kotlinx:serialization-msgpack-timestamp-extension:${kotlinxSerializationMsgPackSnapshotVersion}")
```

## Usage

Library should be used in same way as core msgpack library. The difference is that it is possible to directly serialize and deserialize instances of `Instant` class.

**Example:**
```kotlin
fun encode() {
    println(
        MsgPack.encodeToByteArray(
            MsgPackTimestamp32DatetimeSerializer(),
            Clock.System.now()
        ).joinToString(separator = "") { it.toInt().and(0xff).toString(16).padStart(2, '0') }
    ) // Outputs: d6ff6141bb2c
}

fun decode() {
    println(
        MsgPack.decodeFromByteArray(
            MsgPackTimestamp32DatetimeSerializer(),
            "d6ff6141bb2c".let { bytesString ->
                ByteArray(bytesString.length / 2) { bytesString.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
            }
        )
    ) // Outputs: 2021-09-15T09:21:48Z
}
```
