@file:OptIn(ExperimentalWasmJsInterop::class) @file:JsModule("@msgpack/msgpack")

package com.ensarsarajcic.kotlinx.serialization.msgpack

import js.buffer.ArrayBuffer
import js.typedarrays.Uint8Array

internal external interface EncoderOptions : JsAny
internal external interface DecoderOptions : JsAny

internal external fun encode(obj: JsAny, options: EncoderOptions? = definedExternally): Uint8Array<ArrayBuffer>

internal external fun <T : JsAny> decode(bytes: Uint8Array<*>, options: DecoderOptions? = definedExternally): T