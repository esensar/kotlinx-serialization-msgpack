package com.ensarsarajcic.kotlinx.serialization.msgpack.stream

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MsgPackDataOutputBufferTest {
    @Test
    fun testAddSingleByte() {
        val buffer = MsgPackDataOutputBuffer()

        assertTrue(buffer.toByteArray().isEmpty())

        buffer.add(0x00)

        assertEquals(listOf<Byte>(0x00), buffer.toByteArray().toList())
    }

    @Test
    fun testAddByteArray() {
        val buffer = MsgPackDataOutputBuffer()

        assertTrue(buffer.toByteArray().isEmpty())

        buffer.addAll(byteArrayOf(0x00, 0x01, 0x02))

        assertEquals(listOf<Byte>(0x00, 0x01, 0x02), buffer.toByteArray().toList())

        buffer.addAll(byteArrayOf())

        assertEquals(listOf<Byte>(0x00, 0x01, 0x02), buffer.toByteArray().toList())
    }

    @Test
    fun testAddByteList() {
        val buffer = MsgPackDataOutputBuffer()

        assertTrue(buffer.toByteArray().isEmpty())

        buffer.addAll(listOf(0x00, 0x01, 0x02))

        assertEquals(listOf<Byte>(0x00, 0x01, 0x02), buffer.toByteArray().toList())

        buffer.addAll(listOf())

        assertEquals(listOf<Byte>(0x00, 0x01, 0x02), buffer.toByteArray().toList())
    }
}
