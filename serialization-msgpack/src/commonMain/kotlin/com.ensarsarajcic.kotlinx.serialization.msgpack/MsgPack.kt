package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.BasicMsgPackDecoder
import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.BasicMsgPackEncoder
import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.MsgPackDecoder
import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.MsgPackEncoder
import com.ensarsarajcic.kotlinx.serialization.msgpack.stream.toMsgPackBuffer
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.jvm.JvmOverloads

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
 * ```
 *
 * @see MsgPack.Default The instance using default configurations.
 */
@OptIn(ExperimentalSerializationApi::class)
sealed class MsgPack @JvmOverloads constructor(
    val configuration: MsgPackConfiguration = MsgPackConfiguration.default,
    override val serializersModule: SerializersModule = SerializersModule {
        contextual(Any::class, MsgPackDynamicSerializer)
    },
    private val inlineEncoders: Map<SerialDescriptor, (BasicMsgPackEncoder) -> Encoder> = mapOf(),
    private val inlineDecoders: Map<SerialDescriptor, (BasicMsgPackDecoder) -> Decoder> = mapOf()
) : BinaryFormat {
    companion object Default : MsgPack()

    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        val decoder = MsgPackDecoder(BasicMsgPackDecoder(configuration, serializersModule, bytes.toMsgPackBuffer(), inlineDecoders = inlineDecoders))
        return decoder.decodeSerializableValue(deserializer)
    }

    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val encoder = MsgPackEncoder(BasicMsgPackEncoder(configuration, serializersModule, inlineEncoders = inlineEncoders))
        kotlin.runCatching {
            encoder.encodeSerializableValue(serializer, value)
        }.fold(
            onSuccess = { return encoder.result.toByteArray() },
            onFailure = {
                throw it
            }
        )
    }
}
