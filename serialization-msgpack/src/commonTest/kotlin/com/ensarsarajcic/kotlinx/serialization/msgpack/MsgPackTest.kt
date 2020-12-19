package com.ensarsarajcic.kotlinx.serialization.msgpack

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlin.test.Test
import kotlin.test.assertEquals

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
        assertEquals(null, MsgPack.default.decodeFromByteArray(Boolean.serializer().nullable, byteArrayOf(0xc0.toByte())))
    }

    @Test
    fun testNullEncode() {
        assertEquals("c0", MsgPack.default.encodeToByteArray(Boolean.serializer().nullable, null).toHex())
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

    private fun <T> testEncodePairs(serializer: KSerializer<T>, vararg pairs: Pair<String, T>) {
        pairs.forEach { (expectedResult, value) ->
            assertEquals(expectedResult, MsgPack.default.encodeToByteArray(serializer, value).toHex())
        }
    }
    private fun <T> testDecodePairs(serializer: KSerializer<T>, vararg pairs: Pair<String, T>) {
        pairs.forEach { (value, expectedResult) ->
            assertEquals(expectedResult, MsgPack.default.decodeFromByteArray(serializer, value.hexStringToByteArray()))
        }
    }
}
