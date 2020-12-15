package com.ensarsarajcic.kotlinx.serialization.msgpack.types

import kotlin.experimental.and
import kotlin.experimental.or

internal object MsgPackType {
    internal object Boolean {
        operator fun invoke(value: kotlin.Boolean) = if (value) TRUE else FALSE
        const val TRUE = 0xc3.toByte()
        const val FALSE = 0xc2.toByte()
    }

    internal interface Mask<T> {
        operator fun invoke(value: T) = maskValue(value)
        fun maskValue(value: T): T
        fun test(value: T): kotlin.Boolean
        fun unMaskValue(value: T): T = maskValue(value)
    }

    internal object Int {
        const val INT8 = 0xd0.toByte()
        const val INT16 = 0xd1.toByte()
        const val INT32 = 0xd2.toByte()
        const val INT64 = 0xd3.toByte()
        const val UINT8 = 0xcc.toByte()
        const val UINT16 = 0xcd.toByte()
        const val UINT32 = 0xce.toByte()
        const val UINT64 = 0xcf.toByte()

        val POSITIVE_FIXNUM_MASK = object : Mask<Byte> {
            private val mask = 0b01111111.toByte()
            override fun maskValue(value: Byte): Byte = mask and value
            override fun test(value: Byte): kotlin.Boolean = (mask or value) == mask
        }
        val NEGATIVE_FIXNUM_MASK = object : Mask<Byte> {
            private val mask = 0b11100000
            override fun maskValue(value: Byte): Byte = (mask or value.toInt()).toByte()
            override fun test(value: Byte): kotlin.Boolean = (mask and value.toInt()) == mask
            override fun unMaskValue(value: Byte): Byte = (mask xor value.toInt()).toByte()
        }
        const val MIN_NEGATIVE_BYTE = -32
    }

    const val NULL = 0xc0.toByte()
}
