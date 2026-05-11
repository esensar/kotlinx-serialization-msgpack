@file:OptIn(ExperimentalWasmJsInterop::class)

package com.ensarsarajcic.kotlinx.serialization.msgpack

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

expect fun encodeMsgpack(obj: JsAny): ByteArray

expect fun <T : JsAny> decodeMsgpack(bytes: ByteArray): T
