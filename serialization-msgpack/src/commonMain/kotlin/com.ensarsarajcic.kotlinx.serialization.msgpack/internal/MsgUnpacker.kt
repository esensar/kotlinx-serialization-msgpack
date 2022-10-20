package com.ensarsarajcic.kotlinx.serialization.msgpack.internal

import com.ensarsarajcic.kotlinx.serialization.msgpack.exceptions.MsgPackSerializationException
import com.ensarsarajcic.kotlinx.serialization.msgpack.stream.MsgPackDataInputBuffer
import com.ensarsarajcic.kotlinx.serialization.msgpack.types.MsgPackType
import com.ensarsarajcic.kotlinx.serialization.msgpack.utils.joinToNumber

internal interface MsgUnpacker {
    fun unpackNull()
    fun unpackBoolean(): Boolean
    fun unpackByte(strict: Boolean = false, preventOverflow: Boolean = false): Byte
    fun unpackShort(strict: Boolean = false, preventOverflow: Boolean = false): Short
    fun unpackInt(strict: Boolean = false, preventOverflow: Boolean = false): Int
    fun unpackLong(strict: Boolean = false, preventOverflow: Boolean = false): Long
    fun unpackFloat(strict: Boolean = false): Float
    fun unpackDouble(strict: Boolean = false): Double
    fun unpackString(preventOverflow: Boolean = false): String
    fun unpackByteArray(preventOverflow: Boolean = false): ByteArray
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

    override fun unpackByte(strict: Boolean, preventOverflow: Boolean): Byte {
        // Check is it a single byte value
        val next = dataBuffer.requireNextByte()
        return when {
            MsgPackType.Int.POSITIVE_FIXNUM_MASK.test(next) or MsgPackType.Int.NEGATIVE_FIXNUM_MASK.test(next) -> next
            MsgPackType.Int.isByte(next) -> {
                if (next == MsgPackType.Int.UINT8 && preventOverflow) {
                    val number = (dataBuffer.requireNextByte().toInt() and 0xff).toShort()
                    if (number !in Byte.MIN_VALUE..Byte.MAX_VALUE) {
                        throw MsgPackSerializationException.overflowError(dataBuffer)
                    } else {
                        number.toByte()
                    }
                } else {
                    dataBuffer.requireNextByte()
                }
            }
            else -> throw MsgPackSerializationException.deserialization(dataBuffer, "Expected byte type, but found $next")
        }
    }

    override fun unpackShort(strict: Boolean, preventOverflow: Boolean): Short {
        val next = dataBuffer.peek()
        return when {
            MsgPackType.Int.isShort(next) -> {
                dataBuffer.skip(1)
                if (next == MsgPackType.Int.UINT16 && preventOverflow) {
                    val number = dataBuffer.takeNext(2).joinToNumber<Int>()
                    if (number !in Short.MIN_VALUE..Short.MAX_VALUE) {
                        throw MsgPackSerializationException.overflowError(dataBuffer)
                    } else {
                        number.toShort()
                    }
                } else {
                    dataBuffer.takeNext(2).joinToNumber()
                }
            }
            next == MsgPackType.Int.UINT8 -> {
                dataBuffer.skip(1)
                (dataBuffer.requireNextByte().toInt() and 0xff).toShort()
            }
            else -> if (strict) throw MsgPackSerializationException.deserialization(dataBuffer, "Expected short type, but found $next") else unpackNumber().toShort()
        }
    }

    override fun unpackInt(strict: Boolean, preventOverflow: Boolean): Int {
        val next = dataBuffer.peek()
        return when {
            MsgPackType.Int.isInt(next) -> {
                dataBuffer.skip(1)
                if (next == MsgPackType.Int.UINT32 && preventOverflow) {
                    val number = dataBuffer.takeNext(4).joinToNumber<Long>()
                    if (number !in Int.MIN_VALUE..Int.MAX_VALUE) {
                        throw MsgPackSerializationException.overflowError(dataBuffer)
                    } else {
                        number.toInt()
                    }
                } else {
                    dataBuffer.takeNext(4).joinToNumber()
                }
            }
            next == MsgPackType.Int.UINT16 -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(2).joinToNumber()
            }
            else -> if (strict) throw MsgPackSerializationException.deserialization(dataBuffer, "Expected int type, but found $next") else unpackNumber().toInt()
        }
    }

    override fun unpackLong(strict: Boolean, preventOverflow: Boolean): Long {
        val next = dataBuffer.peek()
        return when {
            MsgPackType.Int.isLong(next) -> {
                dataBuffer.skip(1)
                if (next == MsgPackType.Int.UINT64 && preventOverflow) {
                    val number = dataBuffer.takeNext(8).joinToNumber<Long>()
                    if (number < 0) {
                        throw MsgPackSerializationException.overflowError(dataBuffer)
                    } else {
                        number
                    }
                } else {
                    dataBuffer.takeNext(8).joinToNumber()
                }
            }
            next == MsgPackType.Int.UINT32 -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(4).joinToNumber()
            }
            else -> if (strict) throw MsgPackSerializationException.deserialization(dataBuffer, "Expected long type, but found $next") else unpackNumber().toLong()
        }
    }

    override fun unpackFloat(strict: Boolean): Float {
        return when (val type = dataBuffer.peek()) {
            MsgPackType.Float.FLOAT -> {
                dataBuffer.skip(1)
                Float.fromBits(dataBuffer.takeNext(4).joinToNumber())
            }
            else -> if (strict) throw MsgPackSerializationException.deserialization(dataBuffer, "Expected float type, but found $type") else unpackNumber().toFloat()
        }
    }

    override fun unpackDouble(strict: Boolean): Double {
        return when (val type = dataBuffer.peek()) {
            MsgPackType.Float.DOUBLE -> {
                dataBuffer.skip(1)
                Double.fromBits(dataBuffer.takeNext(8).joinToNumber())
            }
            else -> if (strict) throw MsgPackSerializationException.deserialization(dataBuffer, "Expected double type, but found $type") else unpackNumber().toDouble()
        }
    }

    private fun unpackNumber(): Number {
        return when (val type = dataBuffer.peek()) {
            MsgPackType.Float.DOUBLE -> unpackDouble(strict = true)
            MsgPackType.Float.FLOAT -> unpackFloat(strict = true)
            MsgPackType.Int.UINT64,
            MsgPackType.Int.INT64,
            MsgPackType.Int.UINT32 -> unpackLong(strict = true, false)
            MsgPackType.Int.INT32,
            MsgPackType.Int.UINT16 -> unpackInt(strict = true)
            MsgPackType.Int.INT16,
            MsgPackType.Int.UINT8 -> unpackShort(strict = true)
            MsgPackType.Int.INT8 -> unpackByte(strict = true)
            else -> throw MsgPackSerializationException.deserialization(dataBuffer, "Expected number type, but found $type")
        }
    }

    override fun unpackString(preventOverflow: Boolean): String {
        val next = dataBuffer.requireNextByte()
        val length = when {
            MsgPackType.String.FIXSTR_SIZE_MASK.test(next) -> MsgPackType.String.FIXSTR_SIZE_MASK.unMaskValue(next).toInt()
            next == MsgPackType.String.STR8 -> dataBuffer.requireNextByte().toInt() and 0xff
            next == MsgPackType.String.STR16 -> dataBuffer.takeNext(2).joinToNumber()
            next == MsgPackType.String.STR32 -> {
                if (preventOverflow) {
                    val number = dataBuffer.takeNext(4).joinToNumber<Long>()
                    if (number !in Int.MIN_VALUE..Int.MAX_VALUE) {
                        throw MsgPackSerializationException.overflowError(dataBuffer)
                    } else {
                        number.toInt()
                    }
                } else {
                    dataBuffer.takeNext(4).joinToNumber()
                }
            }
            else -> {
                throw MsgPackSerializationException.deserialization(dataBuffer, "Expected string type, but found $next")
            }
        }
        if (length == 0) return ""
        return dataBuffer.takeNext(length).decodeToString()
    }

    override fun unpackByteArray(preventOverflow: Boolean): ByteArray {
        val next = dataBuffer.requireNextByte()
        val length = when (next) {
            MsgPackType.Bin.BIN8 -> dataBuffer.requireNextByte().toInt() and 0xff
            MsgPackType.Bin.BIN16 -> dataBuffer.takeNext(2).joinToNumber()
            MsgPackType.Bin.BIN32 -> {
                if (preventOverflow) {
                    val number = dataBuffer.takeNext(4).joinToNumber<Long>()
                    if (number !in Int.MIN_VALUE..Int.MAX_VALUE) {
                        throw MsgPackSerializationException.overflowError(dataBuffer)
                    } else {
                        number.toInt()
                    }
                } else {
                    dataBuffer.takeNext(4).joinToNumber()
                }
            }
            else -> {
                throw MsgPackSerializationException.deserialization(dataBuffer, "Expected binary type, but found $next")
            }
        }
        if (length == 0) return byteArrayOf()
        return dataBuffer.takeNext(length)
    }
}
