package com.ensarsarajcic.kotlinx.serialization.msgpack.unsigned

import com.ensarsarajcic.kotlinx.serialization.msgpack.exceptions.MsgPackSerializationException
import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.InlineDecoderHelper
import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.InlineEncoderHelper
import com.ensarsarajcic.kotlinx.serialization.msgpack.unsigned.utils.joinToNumber
import com.ensarsarajcic.kotlinx.serialization.msgpack.unsigned.utils.splitToByteArray
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.AbstractEncoder

private object UnsignedTypes {
    const val UINT8 = 0xcc.toByte()
    const val UINT16 = 0xcd.toByte()
    const val UINT32 = 0xce.toByte()
    const val UINT64 = 0xcf.toByte()
}

class UByteEncoder(private val encoderHelper: InlineEncoderHelper) : AbstractEncoder() {
    override val serializersModule = encoderHelper.serializersModule

    override fun encodeByte(value: Byte) {
        encoderHelper.outputBuffer.add(UnsignedTypes.UINT8)
        encoderHelper.outputBuffer.add(value)
    }
}

class UShortEncoder(private val encoderHelper: InlineEncoderHelper) : AbstractEncoder() {
    override val serializersModule = encoderHelper.serializersModule

    override fun encodeShort(value: Short) {
        encoderHelper.outputBuffer.add(UnsignedTypes.UINT16)
        encoderHelper.outputBuffer.addAll(value.splitToByteArray())
    }
}

class UIntEncoder(private val encoderHelper: InlineEncoderHelper) : AbstractEncoder() {
    override val serializersModule = encoderHelper.serializersModule

    override fun encodeInt(value: Int) {
        encoderHelper.outputBuffer.add(UnsignedTypes.UINT32)
        encoderHelper.outputBuffer.addAll(value.splitToByteArray())
    }
}

class ULongEncoder(private val encoderHelper: InlineEncoderHelper) : AbstractEncoder() {
    override val serializersModule = encoderHelper.serializersModule

    override fun encodeLong(value: Long) {
        encoderHelper.outputBuffer.add(UnsignedTypes.UINT64)
        encoderHelper.outputBuffer.addAll(value.splitToByteArray())
    }
}

class UByteDecoder(private val decoderHelper: InlineDecoderHelper) : AbstractDecoder() {
    override val serializersModule = decoderHelper.serializersModule

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = 0

    override fun decodeByte(): Byte {
        val type = decoderHelper.inputBuffer.requireNextByte()
        if (type != UnsignedTypes.UINT8) throw MsgPackSerializationException.deserialization(decoderHelper.inputBuffer, "Expected UINT8 type, but found $type")
        return decoderHelper.inputBuffer.requireNextByte()
    }
}

class UShortDecoder(private val decoderHelper: InlineDecoderHelper) : AbstractDecoder() {
    override val serializersModule = decoderHelper.serializersModule

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = 0

    override fun decodeShort(): Short {
        val type = decoderHelper.inputBuffer.requireNextByte()
        if (type != UnsignedTypes.UINT16) throw MsgPackSerializationException.deserialization(decoderHelper.inputBuffer, "Expected UINT16 type, but found $type")
        return decoderHelper.inputBuffer.takeNext(2).joinToNumber()
    }
}

class UIntDecoder(private val decoderHelper: InlineDecoderHelper) : AbstractDecoder() {
    override val serializersModule = decoderHelper.serializersModule

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = 0

    override fun decodeInt(): Int {
        val type = decoderHelper.inputBuffer.requireNextByte()
        if (type != UnsignedTypes.UINT32) throw MsgPackSerializationException.deserialization(decoderHelper.inputBuffer, "Expected UINT32 type, but found $type")
        return decoderHelper.inputBuffer.takeNext(4).joinToNumber()
    }
}

class ULongDecoder(private val decoderHelper: InlineDecoderHelper) : AbstractDecoder() {
    override val serializersModule = decoderHelper.serializersModule

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = 0

    override fun decodeLong(): Long {
        val type = decoderHelper.inputBuffer.requireNextByte()
        if (type != UnsignedTypes.UINT64) throw MsgPackSerializationException.deserialization(decoderHelper.inputBuffer, "Expected UINT64 type, but found $type")
        return decoderHelper.inputBuffer.takeNext(8).joinToNumber()
    }
}
