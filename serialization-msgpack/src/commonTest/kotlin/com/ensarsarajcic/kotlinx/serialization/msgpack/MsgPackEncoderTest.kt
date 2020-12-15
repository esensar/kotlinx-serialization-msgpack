package com.ensarsarajcic.kotlinx.serialization.msgpack

import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MsgPackEncoderTest {
    @Test
    fun testTrueEncode() {
        val encoder = MsgPackEncoder(MsgPackConfiguration.default, SerializersModule {})
        encoder.encodeBoolean(true)
        assertEquals("c3", encoder.result.toByteArray().toHexString())
    }

    @Test
    fun testFalseEncode() {
        val encoder = MsgPackEncoder(MsgPackConfiguration.default, SerializersModule {})
        encoder.encodeBoolean(false)
        assertEquals("c2", encoder.result.toByteArray().toHexString())
    }

    @Test
    fun testNullEncode() {
        val encoder = MsgPackEncoder(MsgPackConfiguration.default, SerializersModule {})
        encoder.encodeNull()
        assertEquals("c0", encoder.result.toByteArray().toHexString())
    }

    @Test
    fun testByteEncode() {
        fun testByteEncoding(input: Byte, expectedResult: String) = MsgPackEncoder(MsgPackConfiguration.default, SerializersModule {}).also {
            it.encodeByte(input)
            assertEquals(expectedResult, it.result.toByteArray().toHexString())
        }
        testByteEncoding(55, "37")
        testByteEncoding(-32, "e0")
        testByteEncoding(127, "7f")
        testByteEncoding(0, "00")
        testByteEncoding(-1, "ff")
        testByteEncoding(-2, "fe")
        testByteEncoding(-33, "d0df")
        testByteEncoding(-50, "d0ce")
        testByteEncoding(-127, "d081")
    }

    private fun ByteArray.toHexString() = asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }
}
