# kotlinx-serialization-msgpack

[![Tests](https://github.com/esensar/kotlinx-serialization-msgpack/workflows/Tests/badge.svg)](https://github.com/esensar/kotlinx-serialization-msgpack/actions)
[![javadoc](https://javadoc.io/badge2/com.ensarsarajcic.kotlinx/serialization-msgpack/javadoc.svg)](https://javadoc.io/doc/com.ensarsarajcic.kotlinx/serialization-msgpack)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.ensarsarajcic.kotlinx/serialization-msgpack/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.ensarsarajcic.kotlinx/serialization-msgpack)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.ensarsarajcic.kotlinx/serialization-msgpack?server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/ensarsarajcic/kotlinx/serialization-msgpack/)

**Project is under active development! Important features may be missing and bugs are present!**

Check out [milestones](https://github.com/esensar/kotlinx-serialization-msgpack/milestones) for progress.

## About
This library provides [MsgPack](https://github.com/Kotlin/kotlinx.serialization) support for [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization). It supports all of kotlin targets (JVM, JS, Native).

## Integration

### Maven central
**Gradle:**
```kotlin
implementation("com.ensarsarajcic.kotlinx:serialization-msgpack:${kotlinxSerializationMsgPackVersion}")
```

To also use timestamp support with [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime), use `serialization-msgpack-timestamp-extension`:
```kotlin
implementation("com.ensarsarajcic.kotlinx:serialization-msgpack-timestamp-extension:${kotlinxSerializationMsgPackVersion}")
```

**NOTE**:
Timestamp support is available in core library as well, the additional library just adds a specific serializer that can be used with [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) types. These are `MsgPackTimestamp32DatetimeSerializer`, `MsgPackTimestamp64DatetimeSerializer` and `MsgPackTimestamp96DatetimeSerializer`.

For experimental kotlin unsigned types support, use `serialization-msgpack-unsigned-support`:
```kotlin
implementation("com.ensarsarajcic.kotlinx:serialization-msgpack-unsigned-support:${kotlinxSerializationMsgPackVersion}")
```

### Snapshot builds
**Gradle:**
```kotlin
repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
implementation("com.ensarsarajcic.kotlinx:serialization-msgpack:${kotlinxSerializationMsgPackSnapshotVersion}")
```

To also use timestamp support with [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime), use `serialization-msgpack-timestamp-extension`:

```kotlin
implementation("com.ensarsarajcic.kotlinx:serialization-msgpack-timestamp-extension:${kotlinxSerializationMsgPackSnapshotVersion}")
```

For experimental kotlin unsigned types support, use `serialization-msgpack-unsigned-support`:
```kotlin
implementation("com.ensarsarajcic.kotlinx:serialization-msgpack-unsigned-support:${kotlinxSerializationMsgPackSnapshotVersion}")
```

## Usage

Library should be used in same way as any other kotlinx.serialization library. Created models are annotated with `@Serializable` annotation and their `serializer()` can be passed to `MsgPack`.

**Example:**
```kotlin
@Serializable
data class SampleClass(
    val testString: String,
    val testInt: Int,
    val testBoolean: Boolean
)

fun encode() {
    println(
        MsgPack.encodeToByteArray(
            SampleClass.serializer(),
            SampleClass("def", 123, true)
        ).joinToString(separator = "") { it.toInt().and(0xff).toString(16).padStart(2, '0') }
    ) // Outputs: 83aa74657374537472696e67a3646566a774657374496e747bab74657374426f6f6c65616ec3
}

fun decode() {
    println(
        MsgPack.decodeFromByteArray(
            SampleClass.serializer(),
            "83aa74657374537472696e67a3646566a774657374496e747bab74657374426f6f6c65616ec3".let { bytesString ->
                ByteArray(bytesString.length / 2) { bytesString.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
            }
        )
    ) // Outputs: SampleClass(testString=def, testInt=123, testBoolean=true)
}
```

## Contributing

Check out [contributing guidelines](CONTRIBUTING.md).

## License

[MIT](LICENSE)
