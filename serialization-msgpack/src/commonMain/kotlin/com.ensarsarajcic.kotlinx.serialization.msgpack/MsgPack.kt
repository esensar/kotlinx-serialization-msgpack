package com.ensarsarajcic.kotlinx.serialization.msgpack

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * Main entry point of library
 *
 *
 * ## Examples of usage
 * ```
 * val msgPack = MsgPack(...)
 *
 * @Serializer
 * class Message(val id: Int, val data: String)
 *
 * // parsing from [ByteArray] to an object
 * msgPack.parse(Message.serializer(), binaryData)
 *
 * @see MsgPack.default The instance using default configurations.
 */
@OptIn(ExperimentalSerializationApi::class)
class MsgPack @JvmOverloads constructor(
    val configuration: MsgPackConfiguration = MsgPackConfiguration.default,
    override val serializersModule: SerializersModule = SerializersModule {
        contextual(Any::class, MsgPackDynamicSerializer)
    }
) : BinaryFormat {
    companion object {
        @JvmStatic
        val default: MsgPack = MsgPack()
    }

    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        val decoder = MsgPackDecoder(configuration, serializersModule, bytes)
        return deserializer.deserialize(decoder)
    }

    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val encoder = MsgPackEncoder(configuration, serializersModule)
        kotlin.runCatching {
            serializer.serialize(encoder, value)
        }.fold(
            onSuccess = { return encoder.result.toByteArray() },
            onFailure = {
                throw it
            }
        )
    }
}
