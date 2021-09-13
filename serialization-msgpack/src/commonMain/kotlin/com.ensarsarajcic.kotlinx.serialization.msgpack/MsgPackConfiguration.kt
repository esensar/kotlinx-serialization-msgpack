package com.ensarsarajcic.kotlinx.serialization.msgpack

import kotlin.jvm.JvmStatic

/**
 * MsgPack configuration
 *
 * Provides means of customizing library behavior
 *
 * @see MsgPackConfiguration.default The default configuration
 */
data class MsgPackConfiguration(
    val rawCompatibility: Boolean = false
) {
    companion object {
        @JvmStatic
        val default: MsgPackConfiguration = MsgPackConfiguration()
    }
}
