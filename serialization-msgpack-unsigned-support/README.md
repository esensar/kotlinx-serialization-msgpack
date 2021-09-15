# kotlinx-serialization-msgpack-unsigned-support

[![javadoc](https://javadoc.io/badge2/com.ensarsarajcic.kotlinx/serialization-msgpack-unsigned-support/javadoc.svg)](https://javadoc.io/doc/com.ensarsarajcic.kotlinx/serialization-msgpack-unsigned-support)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.ensarsarajcic.kotlinx/serialization-msgpack-unsigned-support/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.ensarsarajcic.kotlinx/serialization-msgpack-unsigned-support)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.ensarsarajcic.kotlinx/serialization-msgpack-unsigned-support?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/com/ensarsarajcic/kotlinx/serialization-msgpack-unsigned-support/)

## About

This module provides additional kotlin experimental unsigned numbers support. It should be used together with core library, by adding additional coders provided by this module.

## Integration

### Maven central
**Gradle:**
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
implementation("com.ensarsarajcic.kotlinx:serialization-msgpack-unsigned-support:${kotlinxSerializationMsgPackSnapshotVersion}")
```

## Usage

Library should be used in same way as core msgpack library. Coders provided by this library should be added to `MsgPack` instance used.

**Example:**
```kotlin
fun encode() {
    val msgPack = MsgPack(inlineEncoders = MsgPackUnsignedSupport.ALL_ENCODERS, inlineDecoders = MsgPackUnsignedSupport.ALL_DECODERS)
    ...
}

fun decode() {
    val msgPack = MsgPack(inlineEncoders = MsgPackUnsignedSupport.ALL_ENCODERS, inlineDecoders = MsgPackUnsignedSupport.ALL_DECODERS)
    ...
}
```
