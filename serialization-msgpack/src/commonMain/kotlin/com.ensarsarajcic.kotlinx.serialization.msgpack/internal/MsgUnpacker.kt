package com.ensarsarajcic.kotlinx.serialization.msgpack.internal

import com.ensarsarajcic.kotlinx.serialization.msgpack.stream.MsgPackDataInputBuffer
import com.ensarsarajcic.kotlinx.serialization.msgpack.types.MsgPackType
import com.ensarsarajcic.kotlinx.serialization.msgpack.utils.joinToNumber

internal interface MsgUnpacker {
    fun unpackNull()
    fun unpackBoolean(): Boolean
    fun unpackByte(): Byte
    fun unpackShort(): Short
    fun unpackInt(): Int
    fun unpackLong(): Long
    fun unpackFloat(): Float
    fun unpackDouble(): Double
    fun unpackString(): String
    fun unpackByteArray(): ByteArray
}

internal class BasicMsgUnpacker(private val dataBuffer: MsgPackDataInputBuffer) : MsgUnpacker {
    override fun unpackNull() {
        val next = dataBuffer.requireNextByte()
        if (next != MsgPackType.NULL) throw Exception("Invalid null $next")
    }

    override fun unpackBoolean(): Boolean {
        return when (val next = dataBuffer.requireNextByte()) {
            MsgPackType.Boolean.TRUE -> true
            MsgPackType.Boolean.FALSE -> false
            else -> throw Exception("Invalid boolean $next")
        }
    }

    override fun unpackByte(): Byte {
        // Check is it a single byte value
        val next = dataBuffer.requireNextByte()
        return when {
            MsgPackType.Int.POSITIVE_FIXNUM_MASK.test(next) or MsgPackType.Int.NEGATIVE_FIXNUM_MASK.test(next) -> next
            // TODO reader is not handling overflows (when using unsigned types)
            MsgPackType.Int.isByte(next) -> dataBuffer.requireNextByte()
            else -> throw TODO("Add a more descriptive error when wrong type is found!")
        }
    }

    override fun unpackShort(): Short {
        val next = dataBuffer.peek()
        return when {
            MsgPackType.Int.isShort(next) -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(2).joinToNumber()
            }
            next == MsgPackType.Int.UINT8 -> {
                dataBuffer.skip(1)
                (dataBuffer.requireNextByte().toInt() and 0xff).toShort()
            }
            else -> unpackByte().toShort()
        }
    }

    override fun unpackInt(): Int {
        val next = dataBuffer.peek()
        return when {
            MsgPackType.Int.isInt(next) -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(4).joinToNumber()
            }
            next == MsgPackType.Int.UINT16 -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(2).joinToNumber()
            }
            else -> unpackShort().toInt()
        }
    }

    override fun unpackLong(): Long {
        val next = dataBuffer.peek()
        return when {
            MsgPackType.Int.isLong(next) -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(8).joinToNumber()
            }
            next == MsgPackType.Int.UINT32 -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(4).joinToNumber()
            }
            else -> unpackInt().toLong()
        }
    }

    override fun unpackFloat(): Float {
        return when (dataBuffer.peek()) {
            MsgPackType.Float.FLOAT -> {
                dataBuffer.skip(1)
                Float.fromBits(dataBuffer.takeNext(4).joinToNumber())
            }
            else -> TODO("Add a more descriptive error when wrong type is found!")
        }
    }

    override fun unpackDouble(): Double {
        return when (dataBuffer.peek()) {
            MsgPackType.Float.DOUBLE -> {
                dataBuffer.skip(1)
                Double.fromBits(dataBuffer.takeNext(8).joinToNumber())
            }
            MsgPackType.Float.FLOAT -> unpackFloat().toDouble()
            else -> TODO("Add a more descriptive error when wrong type is found!")
        }
    }

    override fun unpackString(): String {
        val next = dataBuffer.requireNextByte()
        val length = when {
            MsgPackType.String.FIXSTR_SIZE_MASK.test(next) -> MsgPackType.String.FIXSTR_SIZE_MASK.unMaskValue(next).toInt()
            next == MsgPackType.String.STR8 -> dataBuffer.requireNextByte().toInt() and 0xff
            next == MsgPackType.String.STR16 -> dataBuffer.takeNext(2).joinToNumber()
            // TODO: this may have issues with long strings, since size will overflow
            next == MsgPackType.String.STR32 -> dataBuffer.takeNext(4).joinToNumber()
            else -> {
                throw TODO("Add a more descriptive error when wrong type is found!")
            }
        }
        if (length == 0) return ""
        return dataBuffer.takeNext(length).decodeToString()
    }

    override fun unpackByteArray(): ByteArray {
        val next = dataBuffer.requireNextByte()
        val length = when (next) {
            MsgPackType.Bin.BIN8 -> dataBuffer.requireNextByte().toInt() and 0xff
            MsgPackType.Bin.BIN16 -> dataBuffer.takeNext(2).joinToNumber()
            // TODO: this may have issues with long byte arrays, since size will overflow
            MsgPackType.Bin.BIN32 -> dataBuffer.takeNext(4).joinToNumber()
            else -> {
                throw TODO("Add a more descriptive error when wrong type is found!")
            }
        }
        if (length == 0) return byteArrayOf()
        return dataBuffer.takeNext(length)
    }
}
