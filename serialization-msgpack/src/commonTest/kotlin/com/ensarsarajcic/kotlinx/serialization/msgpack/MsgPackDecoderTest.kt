package com.ensarsarajcic.kotlinx.serialization.msgpack

import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val decoder = MsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, byteArrayOf(0xc0.toByte()))
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

    private fun <RESULT> testPairs(decodeFunction: MsgPackDecoder.() -> RESULT, vararg pairs: Pair<String, RESULT>) {
        pairs.forEach { (input, result) ->
            MsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray()).also {
                assertEquals(result, it.decodeFunction())
            }
        }
    }
}
