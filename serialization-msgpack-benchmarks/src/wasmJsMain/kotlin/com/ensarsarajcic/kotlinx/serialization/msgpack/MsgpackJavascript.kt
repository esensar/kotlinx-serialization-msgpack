@file:OptIn(ExperimentalWasmJsInterop::class)

package com.ensarsarajcic.kotlinx.serialization.msgpack

import js.typedarrays.toByteArray
import js.typedarrays.toUint8Array

actual fun encodeMsgpack(obj: JsAny): ByteArray = encode(obj).toByteArray()

actual fun <T : JsAny> decodeMsgpack(bytes: ByteArray): T = decode(bytes.toUint8Array())