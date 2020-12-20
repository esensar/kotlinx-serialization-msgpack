package com.ensarsarajcic.kotlinx.serialization.msgpack

import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MsgPackEncoderTest {
    @Test
    fun testBooleanEncode() {
        testPairs(
            MsgPackEncoder::encodeBoolean,
            *TestData.booleanTestPairs
        )
    }

    @Test
    fun testNullEncode() {
        val encoder = MsgPackEncoder(MsgPackConfiguration.default, SerializersModule {})
        encoder.encodeNull()
        assertEquals("c0", encoder.result.toByteArray().toHex())
    }

    @Test
    fun testByteEncode() {
        testPairs(
            MsgPackEncoder::encodeByte,
            *TestData.byteTestPairs
        )
    }

    @Test
    fun testShortEncode() {
        testPairs(
            MsgPackEncoder::encodeShort,
            *TestData.byteTestPairs.map { it.first to it.second.toShort() }.toTypedArray(),
            *TestData.shortTestPairs,
            *TestData.uByteTestPairs,
        )
    }

    @Test
    fun testIntEncode() {
        testPairs(
            MsgPackEncoder::encodeInt,
            *TestData.byteTestPairs.map { it.first to it.second.toInt() }.toTypedArray(),
            *TestData.shortTestPairs.map { it.first to it.second.toInt() }.toTypedArray(),
            *TestData.uByteTestPairs.map { it.first to it.second.toInt() }.toTypedArray(),
            *TestData.intTestPairs,
            *TestData.uShortTestPairs
        )
    }

    @Test
    fun testLongEncode() {
        testPairs(
            MsgPackEncoder::encodeLong,
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
        testPairs(
            MsgPackEncoder::encodeFloat,
            *TestData.floatTestPairs
        )
    }

    @Test
    fun testDoubleEncode() {
        testPairs(
            MsgPackEncoder::encodeDouble,
            *TestData.doubleTestPairs
        )
    }

    @Test
    fun testStringEncode() {
        testPairs(
            MsgPackEncoder::encodeString,
            *TestData.fixStrTestPairs,
            *TestData.str8TestPairs,
            *TestData.str16TestPairs
        )
    }

    @Test
    fun testArrayEncodeStringArrays() {
        TestData.stringArrayTestPairs.forEach { (result, input) ->
            val encoder = MsgPackEncoder(MsgPackConfiguration.default, SerializersModule {})
            val serializer = ArraySerializer(String.serializer())
            serializer.serialize(encoder, input)
            assertEquals(result, encoder.result.toByteArray().toHex())
        }
    }

    @Test
    fun testArrayEncodeIntArrays() {
        TestData.intArrayTestPairs.forEach { (result, input) ->
            val encoder = MsgPackEncoder(MsgPackConfiguration.default, SerializersModule {})
            val serializer = ArraySerializer(Int.serializer())
            serializer.serialize(encoder, input)
            assertEquals(result, encoder.result.toByteArray().toHex())
        }
    }

    @Test
    fun testMapEncode() {
        TestData.mapTestPairs.forEach { (result, input) ->
            val encoder = MsgPackEncoder(MsgPackConfiguration.default, SerializersModule {})
            val serializer = MapSerializer(String.serializer(), String.serializer())
            serializer.serialize(encoder, input)
            assertEquals(result, encoder.result.toByteArray().toHex())
        }
    }

    @Test
    fun testSampleClassEncode() {
        TestData.sampleClassTestPairs.forEach { (result, input) ->
            val encoder = MsgPackEncoder(MsgPackConfiguration.default, SerializersModule {})
            val serializer = TestData.SampleClass.serializer()
            serializer.serialize(encoder, input)
            assertEquals(result, encoder.result.toByteArray().toHex())
        }
    }

    private fun <INPUT> testPairs(encodeFunction: MsgPackEncoder.(INPUT) -> Unit, vararg pairs: Pair<String, INPUT>) {
        pairs.forEach { (result, input) ->
            MsgPackEncoder(MsgPackConfiguration.default, SerializersModule {}).also {
                it.encodeFunction(input)
                assertEquals(result, it.result.toByteArray().toHex())
            }
        }
    }
}
