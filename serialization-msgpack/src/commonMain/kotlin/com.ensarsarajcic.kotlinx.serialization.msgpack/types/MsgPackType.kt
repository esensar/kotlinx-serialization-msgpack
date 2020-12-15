package com.ensarsarajcic.kotlinx.serialization.msgpack.types

internal object MsgPackType {
    internal object Boolean {
        operator fun invoke(value: kotlin.Boolean) = if (value) TRUE else FALSE
        const val TRUE = 0xc3.toByte()
        const val FALSE = 0xc2.toByte()
    }

    const val NULL = 0xc0.toByte()
}
