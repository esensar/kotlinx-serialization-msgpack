@file:JsModule("@msgpack/msgpack") @file:JsNonModule

package com.ensarsarajcic.kotlinx.serialization.msgpack

internal external fun encode(obj: Any): ByteArray

internal external fun <T> decode(bytes: ByteArray): T