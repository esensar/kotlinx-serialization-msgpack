@file:JsModule("@msgpack/msgpack")
@file:JsNonModule

package com.ensarsarajcic.kotlinx.serialization.msgpack

external fun encode(obj: Any): ByteArray

external fun <T> decode(bytes: ByteArray): T
