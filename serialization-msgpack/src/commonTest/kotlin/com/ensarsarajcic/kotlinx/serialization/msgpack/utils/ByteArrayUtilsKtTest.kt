package com.ensarsarajcic.kotlinx.serialization.msgpack.utils

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class ByteArrayUtilsKtTest {
    private val byteTestPairs = arrayOf(
        1.toByte() to byteArrayOf(0x01),
        0.toByte() to byteArrayOf(0x00),
        Byte.MAX_VALUE to byteArrayOf(Byte.MAX_VALUE),
        Byte.MIN_VALUE to byteArrayOf(Byte.MIN_VALUE)
    )
    private val shortTestPairs = arrayOf(
        1.toShort() to byteArrayOf(0x00, 0x01),
        0.toShort() to byteArrayOf(0x00, 0x00),
        Byte.MAX_VALUE.toShort() to byteArrayOf(0x00, Byte.MAX_VALUE),
        256.toShort() to byteArrayOf(0x01, 0x00),
        (-128).toShort() to byteArrayOf(0xff.toByte(), 0x80.toByte()),
        Short.MAX_VALUE to byteArrayOf(0x7f, 0xff.toByte()),
        Short.MIN_VALUE to byteArrayOf(0x80.toByte(), 0x00),
    )
    private val intTestPairs = arrayOf(
        65536 to byteArrayOf(0x00, 0x01, 0x00, 0x00),
        -65000 to byteArrayOf(0xff.toByte(), 0xff.toByte(), 0x02, 0x18),
        -32769 to byteArrayOf(0xff.toByte(), 0xff.toByte(), 0x7f, 0xff.toByte()),
        Int.MAX_VALUE to byteArrayOf(0x7f, 0xff.toByte(), 0xff.toByte(), 0xff.toByte()),
        Int.MIN_VALUE to byteArrayOf(0x80.toByte(), 0x00, 0x00, 0x00)
    )
    private val longTestPairs = arrayOf(
        4294967296 to byteArrayOf(0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00),
        5000000000 to byteArrayOf(0x00, 0x00, 0x00, 0x01, 0x2a, 0x05, 0xf2.toByte(), 0x00),
        9223372036854775807 to byteArrayOf(
            0x7f, 0xff.toByte(), 0xff.toByte(),
            0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte()
        ),
        -2147483649 to byteArrayOf(
            0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte(),
            0x7f.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte()
        ),
        -5000000000 to byteArrayOf(
            0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xfe.toByte(),
            0xd5.toByte(), 0xfa.toByte(), 0x0e.toByte(), 0x00.toByte()
        )
    )

    @Test
    fun testSplitToByteArrayByte() {
        testSplitToByteArrayPairs(byteTestPairs)
    }

    @Test
    fun testJoinToNumberByte() {
        testJoinToNumberPairs(byteTestPairs)
    }

    @Test
    fun testSplitToByteArrayShort() {
        testSplitToByteArrayPairs(shortTestPairs)
    }

    @Test
    fun testJoinToNumberShort() {
        testJoinToNumberPairs(shortTestPairs)
    }

    @Test
    fun testSplitToByteArrayInt() {
        testSplitToByteArrayPairs(intTestPairs)
    }

    @Test
    fun testJoinToNumberInt() {
        testJoinToNumberPairs(intTestPairs)
    }

    @Test
    fun testSplitToByteArrayLong() {
        testSplitToByteArrayPairs(longTestPairs)
    }

    @Test
    fun testJoinToNumberLong() {
        testJoinToNumberPairs(longTestPairs)
    }

    private inline fun <reified T : Number> testSplitToByteArrayPairs(pairs: Array<Pair<T, ByteArray>>) {
        pairs.forEach { (number, expected) ->
            val result = number.splitToByteArray()
            assertContentEquals(expected, result)
        }
    }

    private inline fun <reified T : Number> testJoinToNumberPairs(pairs: Array<Pair<T, ByteArray>>) {
        pairs.forEach { (expected, array) ->
            val result = array.joinToNumber<T>()
            assertEquals(expected, result)
        }
    }
}
