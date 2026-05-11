@file:OptIn(ExperimentalWasmJsInterop::class)

package com.ensarsarajcic.kotlinx.serialization.msgpack

actual fun encodeMsgpack(obj: JsAny): ByteArray = encode(obj)

actual fun <T : JsAny> decodeMsgpack(bytes: ByteArray): T = decode(bytes)