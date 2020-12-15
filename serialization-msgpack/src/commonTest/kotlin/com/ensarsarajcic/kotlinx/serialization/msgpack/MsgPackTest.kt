package com.ensarsarajcic.kotlinx.serialization.msgpack

import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MsgPackTest {
    @Test
    fun testBooleanEncode() {
        assertEquals("c3", MsgPack.default.encodeToByteArray(Boolean.serializer(), true).toHexString())
        assertEquals("c2", MsgPack.default.encodeToByteArray(Boolean.serializer(), false).toHexString())
    }

    @Test
    fun testBooleanDecode() {
        assertEquals(true, MsgPack.default.decodeFromByteArray(Boolean.serializer(), byteArrayOf(0xc3.toByte())))
        assertEquals(false, MsgPack.default.decodeFromByteArray(Boolean.serializer(), byteArrayOf(0xc2.toByte())))
    }

    @Test
    fun testNullDecode() {
        assertEquals(null, MsgPack.default.decodeFromByteArray(Boolean.serializer().nullable, byteArrayOf(0xc0.toByte())))
    }

    @Test
    fun testNullEncode() {
        assertEquals("c0", MsgPack.default.encodeToByteArray(Boolean.serializer().nullable, null).toHexString())
    }

    private fun ByteArray.toHexString() = asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }
}
