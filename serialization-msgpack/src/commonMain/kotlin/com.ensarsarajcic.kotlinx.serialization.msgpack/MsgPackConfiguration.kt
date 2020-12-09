package com.ensarsarajcic.kotlinx.serialization.msgpack

import kotlin.jvm.JvmStatic

/**
 * MsgPack configuration
 *
 * Provides means of customizing library behavior
 *
 * @see MsgPackConfiguration.default The default configuration
 */
class MsgPackConfiguration {
    companion object {
        @JvmStatic
        val default: MsgPackConfiguration = MsgPackConfiguration()
    }
}
