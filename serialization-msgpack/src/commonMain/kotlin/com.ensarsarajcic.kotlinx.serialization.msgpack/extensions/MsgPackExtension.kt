package com.ensarsarajcic.kotlinx.serialization.msgpack.extensions

import kotlinx.serialization.Serializable

@Serializable
class MsgPackExtension(
    val type: Byte,
    val extTypeId: Byte,
    val data: ByteArray
)
