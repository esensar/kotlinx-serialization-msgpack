package com.ensarsarajcic.kotlinx.serialization.msgpack.extensions

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.modules.SerializersModule

class MsgPackExtensionDecoder(
    override val serializersModule: SerializersModule
) : AbstractDecoder() {

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = 0

    override fun decodeValue(): Any {
        return super.decodeValue()
    }
}
