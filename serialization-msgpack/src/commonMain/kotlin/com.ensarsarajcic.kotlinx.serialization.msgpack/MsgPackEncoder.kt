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
}
