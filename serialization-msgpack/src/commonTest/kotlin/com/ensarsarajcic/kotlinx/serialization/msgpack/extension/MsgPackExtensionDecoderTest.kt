package com.ensarsarajcic.kotlinx.serialization.msgpack.extension

import com.ensarsarajcic.kotlinx.serialization.msgpack.CustomExtensionType
import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPackConfiguration
import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackExtension
import com.ensarsarajcic.kotlinx.serialization.msgpack.hexStringToByteArray
import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.BasicMsgPackDecoder
import com.ensarsarajcic.kotlinx.serialization.msgpack.stream.toMsgPackBuffer
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO More tests
internal class MsgPackExtensionDecoderTest {

    @Test
    fun testExtensionDecode() {
        val input = "d40101"
        val expectedOutput = MsgPackExtension(0xd4.toByte(), 0x01, byteArrayOf(0x01))
        val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())
        val serializer = MsgPackExtension.serializer()
        val result = serializer.deserialize(decoder)
        assertEquals(expectedOutput.type, result.type)
        assertEquals(expectedOutput.extTypeId, result.extTypeId)
        assertEquals(expectedOutput.data.toList(), result.data.toList())
    }

    @Test
    fun testExtensionVariableDecode() {
        val input = "c71a016c6a686673646c6a6668736b66687364686b6673646a66687364"
        val expectedOutput = MsgPackExtension(0xc7.toByte(), 0x01, "6c6a686673646c6a6668736b66687364686b6673646a66687364".hexStringToByteArray())
        val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())
        val serializer = MsgPackExtension.serializer()
        val result = serializer.deserialize(decoder)
        assertEquals(expectedOutput.type, result.type)
        assertEquals(expectedOutput.extTypeId, result.extTypeId)
        assertEquals(expectedOutput.data.toList(), result.data.toList())
    }

    @Test
    fun testCustomExtensionDecode() {
        val input = "d5030102"
        val expectedOutput = CustomExtensionType(listOf(0x01, 0x02))
        val decoder = BasicMsgPackDecoder(MsgPackConfiguration.default, SerializersModule {}, input.hexStringToByteArray().toMsgPackBuffer())
        val serializer = CustomExtensionType.serializer()
        val result = serializer.deserialize(decoder)
        assertEquals(expectedOutput.data.toList(), result.data.toList())
    }
}
