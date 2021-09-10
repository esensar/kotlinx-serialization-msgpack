package com.ensarsarajcic.kotlinx.serialization.msgpack.extensions

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class BaseMsgPackExtensionSerializer<T> : KSerializer<T> {
    private val serializer = MsgPackExtension.serializer()

    override fun deserialize(decoder: Decoder): T {
        val extension = decoder.decodeSerializableValue(serializer)
        if (extension.extTypeId != extTypeId) {
            throw TODO("Add more info")
        }
        return deserialize(extension)
    }

    final override val descriptor: SerialDescriptor = serializer.descriptor

    override fun serialize(encoder: Encoder, value: T) {
        val extension = serialize(value)
        if (extension.extTypeId != extTypeId) {
            throw TODO("Add more info")
        }
        encoder.encodeSerializableValue(serializer, serialize(value))
    }

    abstract fun deserialize(extension: MsgPackExtension): T
    abstract fun serialize(extension: T): MsgPackExtension
    abstract val extTypeId: Byte
}
