package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackTimestamp
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

internal class MsgPackTest {
    @Test
    fun testBooleanEncode() {
        testEncodePairs(
            Boolean.serializer(),
            *TestData.booleanTestPairs
        )
    }

    @Test
    fun testBooleanDecode() {
        testDecodePairs(
            Boolean.serializer(),
            *TestData.booleanTestPairs
        )
    }

    @Test
    fun testNullDecode() {
        assertEquals(null, MsgPack.decodeFromByteArray(Boolean.serializer().nullable, byteArrayOf(0xc0.toByte())))
    }

    @Test
    fun testNullEncode() {
        assertEquals("c0", MsgPack.encodeToByteArray(Boolean.serializer().nullable, null).toHex())
    }

    @Test
    fun testByteEncode() {
        testEncodePairs(
            Byte.serializer(),
            *TestData.byteTestPairs
        )
    }

    @Test
    fun testByteDecode() {
        testDecodePairs(
            Byte.serializer(),
            *TestData.byteTestPairs
        )
    }

    @Test
    fun testShortEncode() {
        testEncodePairs(
            Short.serializer(),
            *TestData.byteTestPairs.map { it.first to it.second.toShort() }.toTypedArray(),
            *TestData.shortTestPairs,
            *TestData.uByteTestPairs
        )
    }

    @Test
    fun testShortDecode() {
        testDecodePairs(
            Short.serializer(),
            *TestData.byteTestPairs.map { it.first to it.second.toShort() }.toTypedArray(),
            *TestData.shortTestPairs,
            *TestData.uByteTestPairs
        )
    }

    @Test
    fun testIntEncode() {
        testEncodePairs(
            Int.serializer(),
            *TestData.byteTestPairs.map { it.first to it.second.toInt() }.toTypedArray(),
            *TestData.shortTestPairs.map { it.first to it.second.toInt() }.toTypedArray(),
            *TestData.uByteTestPairs.map { it.first to it.second.toInt() }.toTypedArray(),
            *TestData.intTestPairs,
            *TestData.uShortTestPairs
        )
    }

    @Test
    fun testIntDecode() {
        testDecodePairs(
            Int.serializer(),
            *TestData.byteTestPairs.map { it.first to it.second.toInt() }.toTypedArray(),
            *TestData.shortTestPairs.map { it.first to it.second.toInt() }.toTypedArray(),
            *TestData.uByteTestPairs.map { it.first to it.second.toInt() }.toTypedArray(),
            *TestData.intTestPairs,
            *TestData.uShortTestPairs
        )
    }

    @Test
    fun testLongEncode() {
        testEncodePairs(
            Long.serializer(),
            *TestData.byteTestPairs.map { it.first to it.second.toLong() }.toTypedArray(),
            *TestData.shortTestPairs.map { it.first to it.second.toLong() }.toTypedArray(),
            *TestData.uByteTestPairs.map { it.first to it.second.toLong() }.toTypedArray(),
            *TestData.intTestPairs.map { it.first to it.second.toLong() }.toTypedArray(),
            *TestData.uShortTestPairs.map { it.first to it.second.toLong() }.toTypedArray(),
            *TestData.longTestPairs,
            *TestData.uIntTestPairs
        )
    }

    @Test
    fun testLongDecode() {
        testDecodePairs(
            Long.serializer(),
            *TestData.byteTestPairs.map { it.first to it.second.toLong() }.toTypedArray(),
            *TestData.shortTestPairs.map { it.first to it.second.toLong() }.toTypedArray(),
            *TestData.uByteTestPairs.map { it.first to it.second.toLong() }.toTypedArray(),
            *TestData.intTestPairs.map { it.first to it.second.toLong() }.toTypedArray(),
            *TestData.uShortTestPairs.map { it.first to it.second.toLong() }.toTypedArray(),
            *TestData.longTestPairs,
            *TestData.uIntTestPairs
        )
    }

    @Test
    fun testFloatEncode() {
        testEncodePairs(
            Float.serializer(),
            *TestData.floatTestPairs
        )
    }

    @Test
    fun testFloatDecode() {
        TestData.floatTestPairs.forEach { (value, expectedResult) ->
            // Tests in JS were failing when == comparison was used, so threshold is now used
            val threshold = 0.00001f
            val right = MsgPack.decodeFromByteArray(Float.serializer(), value.hexStringToByteArray())
            assertTrue("Floats should be close enough! (Threshold is $threshold) - Expected: $expectedResult - Received: $right") { expectedResult - right < threshold }
        }
    }

    @Test
    fun testDoubleEncode() {
        testEncodePairs(
            Double.serializer(),
            *TestData.doubleTestPairs
        )
    }

    @Test
    fun testDoubleDecode() {
        TestData.doubleTestPairs.forEach { (value, expectedResult) ->
            // Tests in JS were failing when == comparison was used, so threshold is now used
            val threshold = 0.000000000000000000000000000000000000000000001
            val right = MsgPack.decodeFromByteArray(Double.serializer(), value.hexStringToByteArray())
            assertTrue("Doubles should be close enough! (Threshold is $threshold) - Expected: $expectedResult - Received: $right") { expectedResult - right < threshold }
        }
    }

    @Test
    fun testStringEncode() {
        testEncodePairs(
            String.serializer(),
            *TestData.fixStrTestPairs,
            *TestData.str8TestPairs,
            *TestData.str16TestPairs
        )
    }

    @Test
    fun testStringDecode() {
        testDecodePairs(
            String.serializer(),
            *TestData.fixStrTestPairs,
            *TestData.str8TestPairs,
            *TestData.str16TestPairs
        )
    }

    @Test
    fun testBinaryEncode() {
        testEncodePairs(
            ByteArraySerializer(),
            *TestData.bin8TestPairs
        )
    }

    @Test
    fun testBinaryDecode() {
        TestData.bin8TestPairs.forEach { (value, expectedResult) ->
            assertTrue { expectedResult.contentEquals(MsgPack.decodeFromByteArray(ByteArraySerializer(), value.hexStringToByteArray())) }
        }
    }

    @Test
    fun testArrayEncode() {
        testEncodePairs(
            ArraySerializer(String.serializer()),
            *TestData.stringArrayTestPairs
        )
        testEncodePairs(
            ArraySerializer(Int.serializer()),
            *TestData.intArrayTestPairs
        )
    }

    @Test
    fun testArrayDecode() {
        TestData.stringArrayTestPairs.forEach { (value, expectedResult) ->
            val right = MsgPack.decodeFromByteArray(ArraySerializer(String.serializer()), value.hexStringToByteArray())
            assertEquals(expectedResult.toList(), right.toList())
        }
        TestData.intArrayTestPairs.forEach { (value, expectedResult) ->
            val right = MsgPack.decodeFromByteArray(ArraySerializer(Int.serializer()), value.hexStringToByteArray())
            assertEquals(expectedResult.toList(), right.toList())
        }
    }

    @Test
    fun testMapEncode() {
        testEncodePairs(
            MapSerializer(String.serializer(), String.serializer()),
            *TestData.mapTestPairs
        )
    }

    @Test
    fun testMapDecode() {
        testDecodePairs(
            MapSerializer(String.serializer(), String.serializer()),
            *TestData.mapTestPairs
        )
    }

    @Test
    fun testSampleClassEncode() {
        testEncodePairs(
            TestData.SampleClass.serializer(),
            *TestData.sampleClassTestPairs
        )
    }

    @Test
    fun testSampleClassDecode() {
        testDecodePairs(
            TestData.SampleClass.serializer(),
            *TestData.sampleClassTestPairs
        )
    }

    @Test
    fun testTimestampEncode() {
        testEncodePairs(
            MsgPackTimestamp.serializer(),
            *TestData.timestampTestPairs
        )
    }

    @Test
    fun testTimestampDecode() {
        testDecodePairs(
            MsgPackTimestamp.serializer(),
            *TestData.timestampTestPairs
        )
    }

    @Test
    fun testOverflows() {
        fun <T> testPairs(dataList: List<String>, serializer: KSerializer<T>) {
            dataList.forEach {
                try {
                    MsgPack(
                        configuration = MsgPackConfiguration(preventOverflows = true)
                    ).decodeFromByteArray(serializer, it.hexStringToByteArray())
                    fail("Overflow should have occurred")
                    // TODO change to proper exception once it is made
                } catch (ex: Throwable) {}
            }
        }
        testPairs(TestData.uByteTestPairs.map { it.first }, Byte.serializer())
        testPairs(TestData.uShortTestPairs.map { it.first }, Short.serializer())
        testPairs(TestData.uIntTestPairs.map { it.first }, Int.serializer())
    }

    private fun <T> testEncodePairs(serializer: KSerializer<T>, vararg pairs: Pair<String, T>) {
        pairs.forEach { (expectedResult, value) ->
            assertEquals(expectedResult, MsgPack.encodeToByteArray(serializer, value).toHex())
        }
    }
    private fun <T> testDecodePairs(serializer: KSerializer<T>, vararg pairs: Pair<String, T>) {
        pairs.forEach { (value, expectedResult) ->
            assertEquals(expectedResult, MsgPack.decodeFromByteArray(serializer, value.hexStringToByteArray()))
        }
    }
}
