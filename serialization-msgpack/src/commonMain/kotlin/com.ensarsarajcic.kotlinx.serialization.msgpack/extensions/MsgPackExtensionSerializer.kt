package com.ensarsarajcic.kotlinx.serialization.msgpack.extensions

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class MsgPackExtensionSerializer<T> : KSerializer<T> {

    override fun deserialize(decoder: Decoder): T {
        TODO("Not yet implemented")
    }

    override val descriptor: SerialDescriptor
        get() = TODO("Not yet implemented")

    override fun serialize(encoder: Encoder, value: T) {
        TODO("Not yet implemented")
    }
}
