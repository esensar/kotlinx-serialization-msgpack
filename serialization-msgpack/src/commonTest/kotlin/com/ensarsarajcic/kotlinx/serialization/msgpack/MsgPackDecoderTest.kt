package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackTimestamp
import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.BasicMsgPackDecoder
import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.MsgPackDecoder
import com.ensarsarajcic.kotlinx.serialization.msgpack.stream.toMsgPackBuffer
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MsgPackDecoderTest {
    @Test
    fun testBooleanDecode() {
        testPairs(
            MsgPackDecoder::decodeBoolean,
            *TestData.booleanTestPairs
        )
    }

    @Test
    fun testNullDecode() {
        val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, byteArrayOf(0xc0.toByte()).toMsgPackBuffer())
        assertEquals(null, decoder.decodeNull())
    }

    @Test
    fun testByteDecode() {
        testPairs(
            MsgPackDecoder::decodeByte,
            *TestData.byteTestPairs
        )
    }

    @Test
    fun testShortDecode() {
        testPairs(
            MsgPackDecoder::decodeShort,
            *TestData.byteTestPairs.map { it.first to it.second.toShort() }.toTypedArray(),
            *TestData.shortTestPairs,
            *TestData.uByteTestPairs
        )
    }

    @Test
    fun testIntDecode() {
        testPairs(
            MsgPackDecoder::decodeInt,
            *TestData.byteTestPairs.map { it.first to it.second.toInt() }.toTypedArray(),
            *TestData.shortTestPairs.map { it.first to it.second.toInt() }.toTypedArray(),
            *TestData.uByteTestPairs.map { it.first to it.second.toInt() }.toTypedArray(),
            *TestData.intTestPairs,
            *TestData.uShortTestPairs,
        )
    }

    @Test
    fun testLongDecode() {
        testPairs(
            MsgPackDecoder::decodeLong,
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
    fun testFloatDecode() {
        TestData.floatTestPairs.forEach { (input, result) ->
            BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer()).also {
            // Tests in JS were failing when == comparison was used, so threshold is now used
            val threshold = 0.00001f
            val right = it.decodeFloat()
            assertTrue("Floats should be close enough! (Threshold is $threshold) - Expected: $result - Received: $right") { result - right < threshold }
        }
        }
    }

    @Test
    fun testDoubleDecode() {
        TestData.doubleTestPairs.forEach { (input, result) ->
            BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer()).also {
            // Tests in JS were failing when == comparison was used, so threshold is now used
            val threshold = 0.000000000000000000000000000000000000000000001
            val right = it.decodeDouble()
            assertTrue("Doubles should be close enough! (Threshold is $threshold) - Expected: $result - Received: $right") { result - right < threshold }
        }
        }
    }

    @Test
    fun testStringDecode() {
        testPairs(
            MsgPackDecoder::decodeString,
            *TestData.fixStrTestPairs,
            *TestData.str8TestPairs,
            *TestData.str16TestPairs
        )
    }

    @Test
    fun testByteArrayDecode() {
        TestData.bin8TestPairs.forEach { (input, result) ->
            BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer()).also {
            assertTrue { result.contentEquals(it.decodeSerializableValue(serializer())) }
        }
        }
    }

    @Test
    fun testArrayDecodeStringArrays() {
        TestData.stringArrayTestPairs.forEach { (input, result) ->
            val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())
            val serializer = ArraySerializer(String.serializer())
            assertEquals(result.toList(), serializer.deserialize(decoder).toList())
        }
    }

    @Test
    fun testArrayDecodeIntArrays() {
        TestData.intArrayTestPairs.forEach { (input, result) ->
            val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())
            val serializer = ArraySerializer(Int.serializer())
            assertEquals(result.toList(), serializer.deserialize(decoder).toList())
        }
    }

    @Test
    fun testMapDecode() {
        TestData.mapTestPairs.forEach { (input, result) ->
            val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())
            val serializer = MapSerializer(String.serializer(), String.serializer())
            assertEquals(result, serializer.deserialize(decoder))
        }
    }

    @Test
    fun testSampleClassDecode() {
        TestData.sampleClassTestPairs.forEach { (input, result) ->
            val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())
            val serializer = TestData.SampleClass.serializer()
            assertEquals(result, serializer.deserialize(decoder))
        }
    }

    @Test
    fun testSampleClassWithNestedValueAndMissingKeys() {
        TestData.nestedSampleClassWithMissingValue.forEach { (input, result) ->
            val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default.copy(ignoreUnknownKeys = true), SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())
            val serializer = TestData.SampleClassWithNestedClass.serializer()
            assertEquals(result, serializer.deserialize(decoder))
        }
    }

    @Test
    fun testTimestampDecode() {
        TestData.timestampTestPairs.forEach { (input, result) ->
            val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())
            val serializer = MsgPackTimestamp.serializer()
            assertEquals(result, serializer.deserialize(decoder))
        }
    }

    @Test
    fun testEnumDecode() {
        TestData.enumTestPairs.forEach { (input, result) ->
            val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())
            val serializer = Vocation.serializer()
            assertEquals(result, serializer.deserialize(decoder))
        }
    }

    @Test
    fun testDecodeIgnoreUnknownKeys() {
        TestData.unknownKeysTestPairs.forEach { (input, result) ->
            val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default.copy(ignoreUnknownKeys = true), SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())
            val serializer = TestData.SampleClass.serializer()
            assertEquals(result, serializer.deserialize(decoder))
        }
    }

    @Test
    fun testDecodeNonStrictNumbers() {
        TestData.floatEncodedDataTestPairs.forEach { (input, result) ->
            val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default.copy(strictTypes = false), SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())
            val serializer = TestData.SampleClass.serializer()
            assertEquals(result, serializer.deserialize(decoder))
        }
    }

    private fun <RESULT> testPairs(decodeFunction: MsgPackDecoder.() -> RESULT, vararg pairs: Pair<String, RESULT>) {
        pairs.forEach { (input, result) ->
            MsgPackDecoder(BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())).also {
            assertEquals(result, it.decodeFunction())
        }
        }
    }
}
