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
    /**
     * Enables backwards compatibility with MsgPack 1.0 spec which has raw type instead of string
     * Disables use of bin types and str8 types in serialization and support deserializing string types as bytearrays
     */
    val rawCompatibility: Boolean = false,
    /**
     * Enables strict mode type, which forces exact types when deserializing
     * (not allowing INT16 to be deserialized in place of Int for example)
     */
    val strictTypes: Boolean = false,
    /**
     * Prevent overflows by throwing when overflow would occur
     * Useful when combined with strict mode type
     * true by default
     */
    val preventOverflows: Boolean = true
) {
    companion object {
        @JvmStatic
        val default: MsgPackConfiguration = MsgPackConfiguration()
    }
}
