package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.types.MsgPackType
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.SerializersModule

internal class MsgPackEncoder(
    private val configuration: MsgPackConfiguration,
    override val serializersModule: SerializersModule
) : AbstractEncoder() {
    // TODO this may not be a list in the end
    //      It sould probably be delegated to something
    val result = mutableListOf<Byte>()

    override fun encodeBoolean(value: Boolean) {
        result.add(MsgPackType.Boolean(value))
    }

    override fun encodeNull() {
        result.add(MsgPackType.NULL)
    }

    override fun encodeByte(value: Byte) {
        if (value >= MsgPackType.Int.MIN_NEGATIVE_SINGLE_BYTE) {
            result.add(value)
        } else {
            result.add(MsgPackType.Int.INT8)
            result.add(value)
        }
    }

    override fun encodeShort(value: Short) {
        if (value in MsgPackType.Int.MIN_NEGATIVE_BYTE..Byte.MAX_VALUE) {
            encodeByte(value.toByte())
        } else {
            var uByte = false
            result.add(
                when {
                    value < 0 -> MsgPackType.Int.INT16
                    value <= MsgPackType.Int.MAX_UBYTE -> MsgPackType.Int.UINT8.also { uByte = true }
                    else -> MsgPackType.Int.UINT16
                }
            )
            if (uByte) {
                result.add((value.toInt() and 0xff).toByte())
            } else {
                result.addAll(value.splitToByteArray().toList())
            }
        }
    }

    override fun encodeInt(value: Int) {
        if (value in Short.MIN_VALUE..Short.MAX_VALUE) {
            encodeShort(value.toShort())
        } else {
            var uShort = false
            result.add(
                when {
                    value < 0 -> MsgPackType.Int.INT32
                    value <= MsgPackType.Int.MAX_USHORT -> MsgPackType.Int.UINT16.also { uShort = true }
                    else -> MsgPackType.Int.UINT32
                }
            )
            if (uShort) {
                result.addAll(value.toShort().splitToByteArray().toList())
            } else {
                result.addAll(value.splitToByteArray().toList())
            }
        }
    }

    override fun encodeLong(value: Long) {
        if (value in Int.MIN_VALUE..Int.MAX_VALUE) {
            encodeInt(value.toInt())
        } else {
            var uInt = false
            result.add(
                when {
                    value < 0 -> MsgPackType.Int.INT64
                    value <= MsgPackType.Int.MAX_UINT -> MsgPackType.Int.UINT32.also { uInt = true }
                    else -> MsgPackType.Int.UINT64
                }
            )
            if (uInt) {
                result.addAll(value.toInt().splitToByteArray().toList())
            } else {
                result.addAll(value.splitToByteArray().toList())
            }
        }
    }

    private inline fun <reified T : Number> T.splitToByteArray(): ByteArray {
        val byteCount = when (T::class) {
            Byte::class -> 1
            Short::class -> 2
            Int::class -> 4
            Long::class -> 8
            else -> throw UnsupportedOperationException("Can't split number of type ${T::class} to bytes!")
        }

        val result = ByteArray(byteCount)
        (byteCount - 1).downTo(0).forEach {
            result[byteCount - (it + 1)] = ((this.toLong() shr (8 * it)) and 0xff).toByte()
        }
        return result
    }
}
