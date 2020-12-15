package com.ensarsarajcic.kotlinx.serialization.msgpack

import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MsgPackDecoderTest {
    @Test
    fun testTrueDecode() {
        val decoder = MsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, byteArrayOf(0xc3.toByte()))
        assertEquals(true, decoder.decodeBoolean())
    }

    @Test
    fun testFalseDecode() {
        val decoder = MsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, byteArrayOf(0xc2.toByte()))
        assertEquals(false, decoder.decodeBoolean())
    }

    @Test
    fun testNullDecode() {
        val decoder = MsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, byteArrayOf(0xc0.toByte()))
        assertEquals(null, decoder.decodeNull())
    }
}
