package com.ensarsarajcic.kotlinx.serialization.msgpack

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MsgPackDynamicSerializer : KSerializer<Any> {
    override fun deserialize(decoder: Decoder): Any {
        TODO("Not yet implemented")
    }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MsgPackDynamic")

    override fun serialize(encoder: Encoder, value: Any) {
        TODO("Not yet implemented")
    }
}
