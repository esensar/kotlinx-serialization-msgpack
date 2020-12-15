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

    private fun ByteArray.toHexString() = asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }
}
