package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.types.MsgPackType
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.SerializersModule

internal class MsgPackEncoder(
    private val configuration: MsgPackConfiguration,
    override val serializersModule: SerializersModule
) : AbstractEncoder() {
    val result = mutableListOf<Byte>()

    override fun encodeBoolean(value: Boolean) {
        result.add(MsgPackType.Boolean(value))
    }

    override fun encodeNull() {
        result.add(MsgPackType.NULL)
    }

    override fun encodeByte(value: Byte) {
        if (value >= MsgPackType.Int.MIN_NEGATIVE_BYTE) {
            result.add(value)
        } else {
            result.add(MsgPackType.Int.INT8)
            result.add(value)
        }
    }

    override fun encodeShort(value: Short) {
        if (value in Byte.MIN_VALUE..Byte.MAX_VALUE) {
            encodeByte(value.toByte())
        } else {
            if (value < 0) {
                result.add(MsgPackType.Int.INT16)
                result.add((value.toInt() shr 8).toByte())
                result.add(value.toByte())
            } else {
                result.add(MsgPackType.Int.UINT16)
                result.add((value.toInt() shr 8).toByte())
                result.add(value.toByte())
            }
        }
    }
}
