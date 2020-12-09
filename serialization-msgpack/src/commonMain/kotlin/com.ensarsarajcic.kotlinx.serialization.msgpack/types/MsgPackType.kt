package com.ensarsarajcic.kotlinx.serialization.msgpack.types

object MsgPackType {
    object Boolean {
        operator fun invoke(value: kotlin.Boolean) = if (value) TRUE else FALSE
        const val TRUE = 0xc3.toByte()
        const val FALSE = 0xc2.toByte()
    }
}
