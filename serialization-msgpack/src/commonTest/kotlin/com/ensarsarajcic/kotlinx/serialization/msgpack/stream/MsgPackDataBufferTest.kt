package com.ensarsarajcic.kotlinx.serialization.msgpack.stream

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

internal class MsgPackDataBufferTest {

    @Test
    fun testEmptyBuffer() {
        val buffer = MsgPackDataBuffer(byteArrayOf())

        try {
            buffer.peek()
            fail("Peeking in empty buffer should fail!")
        } catch (e: Exception) {
        }

        try {
            buffer.requireNextByte()
            fail("Requiring next byte in empty buffer should fail!")
        } catch (e: Exception) {
        }

        assertNull(buffer.nextByteOrNull())
    }

    @Test
    fun testBuffer() {
        val buffer = MsgPackDataBuffer(byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06))

        assertEquals(0x00, buffer.peek())
        assertEquals(0x00, buffer.nextByteOrNull())
        assertEquals(0x01, buffer.requireNextByte())
        assertEquals(byteArrayOf(0x02, 0x03).toList(), buffer.takeNext(2).toList())
        assertEquals(byteArrayOf(0x04, 0x05).toList(), buffer.takeNext(2).toList())
        assertEquals(0x06, buffer.requireNextByte())
        try {
            buffer.peek()
            fail("Peeking in at the end of buffer should fail!")
        } catch (e: Exception) {
        }

        try {
            buffer.requireNextByte()
            fail("Requiring next byte should fail!")
        } catch (e: Exception) {
        }

        assertNull(buffer.nextByteOrNull())

        buffer.skip(-7)
        assertEquals(0x00, buffer.peek())
    }
}
