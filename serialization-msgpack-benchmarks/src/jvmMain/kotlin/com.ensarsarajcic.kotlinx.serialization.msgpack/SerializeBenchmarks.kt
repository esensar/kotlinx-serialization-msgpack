package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import org.msgpack.jackson.dataformat.MessagePackFactory

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@Measurement(iterations = 20, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@State(Scope.Benchmark)
open class SerializeBenchmarks {
    @Serializable
    data class SampleClass(
        @SerialName("testString") val testString: String,
        val testInt: Int,
        val testBoolean: Boolean,
    )

    @Serializable
    data class SampleClassWithNestedClass(
        val testString: String,
        val testInt: Int,
        val testBoolean: Boolean,
        val testNested: NestedClass,
        val testSample: SampleClass,
        val testSampleList: List<SampleClass>,
        val testSampleMap: Map<String, SampleClass>,
        val extraBytes: ByteArray,
        val secondNested: NestedClass? = null,
    ) {
        @Serializable
        data class NestedClass(
            val testInt: Int? = null,
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SampleClassWithNestedClass

            if (testInt != other.testInt) return false
            if (testBoolean != other.testBoolean) return false
            if (testString != other.testString) return false
            if (testNested != other.testNested) return false
            if (testSample != other.testSample) return false
            if (testSampleList != other.testSampleList) return false
            if (testSampleMap != other.testSampleMap) return false
            if (!extraBytes.contentEquals(other.extraBytes)) return false
            if (secondNested != other.secondNested) return false

            return true
        }

        override fun hashCode(): Int {
            var result = testInt
            result = 31 * result + testBoolean.hashCode()
            result = 31 * result + testString.hashCode()
            result = 31 * result + testNested.hashCode()
            result = 31 * result + testSample.hashCode()
            result = 31 * result + testSampleList.hashCode()
            result = 31 * result + testSampleMap.hashCode()
            result = 31 * result + extraBytes.contentHashCode()
            result = 31 * result + (secondNested?.hashCode() ?: 0)
            return result
        }
    }

    // The actual benchmark method
    @Benchmark
    fun benchmarkKotlinxSerializationMsgpack() {
        val instance = SampleClassWithNestedClass(
            testString = "testString",
            testInt = 10,
            testBoolean = true,
            testNested = SampleClassWithNestedClass.NestedClass(5),
            testSample = SampleClass(testString = "testString2", testInt = 17, testBoolean = false),
            testSampleList = listOf(
                SampleClass("testString3", testInt = 25, testBoolean = true),
                SampleClass("testString4", testInt = 100, testBoolean = false),
            ),
            testSampleMap = mapOf(
                "testString5" to SampleClass("testString5", testInt = 12, testBoolean = false),
                "testString6" to SampleClass("testString6", testInt = 15, testBoolean = true),
            ),
            extraBytes = byteArrayOf(0x12, 0x13, 0x14, 0x15),
            secondNested = SampleClassWithNestedClass.NestedClass(null),
        )
        MsgPack.encodeToByteArray(instance)
    }

    @Benchmark
    fun benchmarkMsgpackJava() {
        val instance = SampleClassWithNestedClass(
            testString = "testString",
            testInt = 10,
            testBoolean = true,
            testNested = SampleClassWithNestedClass.NestedClass(5),
            testSample = SampleClass(testString = "testString2", testInt = 17, testBoolean = false),
            testSampleList = listOf(
                SampleClass("testString3", testInt = 25, testBoolean = true),
                SampleClass("testString4", testInt = 100, testBoolean = false),
            ),
            testSampleMap = mapOf(
                "testString5" to SampleClass("testString5", testInt = 12, testBoolean = false),
                "testString6" to SampleClass("testString6", testInt = 15, testBoolean = true),
            ),
            extraBytes = byteArrayOf(0x12, 0x13, 0x14, 0x15),
            secondNested = SampleClassWithNestedClass.NestedClass(null),
        )
        val objectMapper = ObjectMapper(MessagePackFactory())
        objectMapper.writeValueAsBytes(instance)
    }
}
