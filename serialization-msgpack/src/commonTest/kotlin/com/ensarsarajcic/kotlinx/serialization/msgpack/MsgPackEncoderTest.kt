package com.ensarsarajcic.kotlinx.serialization.msgpack

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

    private fun <INPUT> testPairs(encodeFunction: MsgPackEncoder.(INPUT) -> Unit, vararg pairs: Pair<String, INPUT>) {
        pairs.forEach { (result, input) ->
            MsgPackEncoder(MsgPackConfiguration.default, SerializersModule {}).also {
                it.encodeFunction(input)
                assertEquals(result, it.result.toByteArray().toHex())
            }
        }
    }
}
