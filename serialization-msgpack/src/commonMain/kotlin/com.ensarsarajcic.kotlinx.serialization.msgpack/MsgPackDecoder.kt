package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.types.MsgPackType
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.modules.SerializersModule

internal class MsgPackDecoder(
    private val configuration: MsgPackConfiguration,
    override val serializersModule: SerializersModule,
    private val byteArray: ByteArray
) : AbstractDecoder() {
    // TODO extract into some form of ByteStream
    private var index = 0
    private fun nextByteOrNull(): Byte? = byteArray.getOrNull(index++)
    private fun requireNextByte(): Byte = nextByteOrNull() ?: throw Exception("End of stream")

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return 0
    }

    override fun decodeNotNullMark(): Boolean {
        val next = byteArray.getOrNull(index) ?: throw Exception("End of stream")
        return next != MsgPackType.NULL
    }

    override fun decodeNull(): Nothing? {
        val next = requireNextByte()
        return if (next == MsgPackType.NULL) null else throw Exception("Invalid null $next")
    }

    override fun decodeBoolean(): Boolean {
        return when (val next = requireNextByte()) {
            MsgPackType.Boolean.TRUE -> true
            MsgPackType.Boolean.FALSE -> false
            else -> throw Exception("Invalid boolean $next")
        }
    }

    override fun decodeByte(): Byte {
        // Check is it a single byte value
        val next = requireNextByte()
        return when {
            MsgPackType.Int.POSITIVE_FIXNUM_MASK.test(next) or MsgPackType.Int.NEGATIVE_FIXNUM_MASK.test(next) -> next
            MsgPackType.Int.isByte(next) -> nextByteOrNull() ?: throw Exception("End of stream")
            else -> throw TODO("Add a more descriptive error when wrong type is found!")
        }
    }

    override fun decodeShort(): Short {
        // Check is it a single byte value
        val next = byteArray.getOrNull(index) ?: throw Exception("End of stream")
        return when {
            MsgPackType.Int.isShort(next) -> {
                index++
                (((requireNextByte().toInt() and 0xff) shl 8) or (requireNextByte().toInt() and 0xff)).toShort()
            }
            else -> decodeByte().toShort()
        }
    }
}
