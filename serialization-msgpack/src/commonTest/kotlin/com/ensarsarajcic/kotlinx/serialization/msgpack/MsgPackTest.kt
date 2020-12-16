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

    @Test
    fun testByteEncode() {
        assertEquals("7f", MsgPack.default.encodeToByteArray(Byte.serializer(), 127).toHexString())
        assertEquals("00", MsgPack.default.encodeToByteArray(Byte.serializer(), 0).toHexString())
        assertEquals("d081", MsgPack.default.encodeToByteArray(Byte.serializer(), -127).toHexString())
        assertEquals("32", MsgPack.default.encodeToByteArray(Byte.serializer(), 50).toHexString())
    }

    @Test
    fun testByteDecode() {
        assertEquals(127, MsgPack.default.decodeFromByteArray(Byte.serializer(), byteArrayOf(0x7f)))
        assertEquals(0, MsgPack.default.decodeFromByteArray(Byte.serializer(), byteArrayOf(0x00)))
        assertEquals(-127, MsgPack.default.decodeFromByteArray(Byte.serializer(), byteArrayOf(0xd0.toByte(), 0x81.toByte())))
        assertEquals(50, MsgPack.default.decodeFromByteArray(Byte.serializer(), byteArrayOf(0x32)))
    }

    @Test
    fun testShortEncode() {
        assertEquals("37", MsgPack.default.encodeToByteArray(Short.serializer(), 55).toHexString())
        assertEquals("cd7fff", MsgPack.default.encodeToByteArray(Short.serializer(), 32767).toHexString())
        assertEquals("d18001", MsgPack.default.encodeToByteArray(Short.serializer(), -32767).toHexString())
        assertEquals("7b", MsgPack.default.encodeToByteArray(Short.serializer(), 123).toHexString())
        assertEquals("d1fb2e", MsgPack.default.encodeToByteArray(Short.serializer(), -1234).toHexString())
        assertEquals("00", MsgPack.default.encodeToByteArray(Short.serializer(), 0).toHexString())
    }

    @Test
    fun testShortDecode() {
        assertEquals(55, MsgPack.default.decodeFromByteArray(Short.serializer(), byteArrayOf(0x37)))
        assertEquals(32767, MsgPack.default.decodeFromByteArray(Short.serializer(), byteArrayOf(0xcd.toByte(), 0x7f, 0xff.toByte())))
        assertEquals(-32767, MsgPack.default.decodeFromByteArray(Short.serializer(), byteArrayOf(0xd1.toByte(), 0x80.toByte(), 0x01)))
        assertEquals(123, MsgPack.default.decodeFromByteArray(Short.serializer(), byteArrayOf(0x7b)))
        assertEquals(-1234, MsgPack.default.decodeFromByteArray(Short.serializer(), byteArrayOf(0xd1.toByte(), 0xfb.toByte(), 0x2e)))
        assertEquals(0, MsgPack.default.decodeFromByteArray(Short.serializer(), byteArrayOf(0x00)))
    }

    private fun ByteArray.toHexString() = asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }
}
