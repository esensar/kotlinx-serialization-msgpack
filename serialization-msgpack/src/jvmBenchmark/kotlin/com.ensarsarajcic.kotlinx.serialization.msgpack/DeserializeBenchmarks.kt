package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.BasicMsgPackDecoder
import com.ensarsarajcic.kotlinx.serialization.msgpack.stream.toMsgPackBuffer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import org.msgpack.jackson.dataformat.MessagePackFactory

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Measurement(iterations = 20, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@State(Scope.Benchmark)
open class DeserializeBenchmarks {
    @Serializable
    @JsonIgnoreProperties(ignoreUnknown = true)
    class SampleClassWithNestedClass(
        @JsonProperty("testString")
        var testString: String,
        @JsonProperty("testInt")
        var testInt: Int,
        @JsonProperty("testBoolean")
        var testBoolean: Boolean,
        @JsonProperty("testNested")
        var testNested: NestedClass,
        @JsonProperty("secondNested")
        var secondNested: NestedClass? = null,
    ) {
        constructor() : this("", 0, false, NestedClass(null), null)

        @Serializable
        @JsonIgnoreProperties(ignoreUnknown = true)
        class NestedClass(
            @JsonProperty("testInt")
            var testInt: Int? = null,
        ) {
            constructor() : this(null)
        }
    }

    // The actual benchmark method
    @Benchmark
    fun benchmarkKotlinxSerializationMsgpack() {
        val decoder =
            BasicMsgPackDecoder(
                MsgPackConfiguration.default.copy(ignoreUnknownKeys = true),
                SerializersModule {
                },
                @Suppress("ktlint:standard:max-line-length")
                "85aa74657374537472696e67a3646566a774657374496e747bab74657374426f6f6c65616ec3aa746573744e657374656483aa74657374537472696e67a3646566ab74657374426f6f6c65616ec3ae616e6f74686572556e6b6e6f776ea474657374ac7365636f6e644e657374656481ab74657374426f6f6c65616ec2".hexStringToByteArray().toMsgPackBuffer(),
            )
        SampleClassWithNestedClass.serializer().deserialize(decoder)
    }

    @Benchmark
    fun benchmarkMsgpackJava() {
        val objectMapper = ObjectMapper(MessagePackFactory())
        objectMapper.readValue(
            @Suppress("ktlint:standard:max-line-length")
            "85aa74657374537472696e67a3646566a774657374496e747bab74657374426f6f6c65616ec3aa746573744e657374656483aa74657374537472696e67a3646566ab74657374426f6f6c65616ec3ae616e6f74686572556e6b6e6f776ea474657374ac7365636f6e644e657374656481ab74657374426f6f6c65616ec2".hexStringToByteArray(),
            object : TypeReference<SampleClassWithNestedClass>() {},
        )
    }
}

fun String.hexStringToByteArray() = ByteArray(this.length / 2) { this.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
