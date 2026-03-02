package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.exceptions.MsgPackSerializationException
import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.DynamicMsgPackExtensionSerializer
import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.MsgPackTypeDecoder
import com.ensarsarajcic.kotlinx.serialization.msgpack.types.MsgPackType
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapEntrySerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

open class MsgPackDynamicSerializer(
    private val nullableSerializer: MsgPackNullableDynamicSerializer = MsgPackNullableDynamicSerializer,
) : KSerializer<Any> {
    companion object Default : MsgPackDynamicSerializer(MsgPackNullableDynamicSerializer)

    final override fun deserialize(decoder: Decoder): Any {
        return nullableSerializer.deserialize(decoder)!!
    }

    @OptIn(InternalSerializationApi::class)
    final override val descriptor: SerialDescriptor = buildSerialDescriptor("MsgPackDynamic", SerialKind.CONTEXTUAL)

    final override fun serialize(
        encoder: Encoder,
        value: Any,
    ) {
        nullableSerializer.serialize(encoder, value)
    }
}

open class MsgPackNullableDynamicSerializer(
    private val dynamicMsgPackExtensionSerializer: DynamicMsgPackExtensionSerializer = DynamicMsgPackExtensionSerializer,
) : KSerializer<Any?> {
    companion object Default : MsgPackNullableDynamicSerializer(DynamicMsgPackExtensionSerializer)

    final override fun deserialize(decoder: Decoder): Any? {
        if (decoder !is MsgPackTypeDecoder) throw MsgPackSerializationException.dynamicSerializationError("Unsupported decoder: $decoder")
        val type = decoder.peekNextType()
        return when {
            type == MsgPackType.NULL -> decoder.decodeNull()
            type == MsgPackType.Boolean.FALSE || type == MsgPackType.Boolean.TRUE -> decoder.decodeBoolean()
            MsgPackType.Int.POSITIVE_FIXNUM_MASK.test(type) ||
                MsgPackType.Int.NEGATIVE_FIXNUM_MASK.test(type) ||
                type == MsgPackType.Int.INT8 -> decoder.decodeByte()
            type == MsgPackType.Int.INT16 || type == MsgPackType.Int.UINT8 -> {
                val result = decoder.decodeShort()
                if (type == MsgPackType.Int.UINT8 && result <= Byte.MAX_VALUE && result >= Byte.MIN_VALUE) {
                    result.toByte()
                } else {
                    result
                }
            }
            type == MsgPackType.Int.INT32 || type == MsgPackType.Int.UINT16 -> {
                val result = decoder.decodeInt()
                if (type == MsgPackType.Int.UINT16 && result <= Short.MAX_VALUE && result >= Short.MIN_VALUE) {
                    result.toShort()
                } else {
                    result
                }
            }
            type == MsgPackType.Int.INT64 || type == MsgPackType.Int.UINT32 || type == MsgPackType.Int.UINT64 -> {
                val result = decoder.decodeLong()
                if (type == MsgPackType.Int.UINT32 && result <= Int.MAX_VALUE && result >= Int.MIN_VALUE) {
                    result.toInt()
                } else {
                    result
                }
            }
            type == MsgPackType.Float.FLOAT -> decoder.decodeFloat()
            type == MsgPackType.Float.DOUBLE -> decoder.decodeDouble()
            MsgPackType.String.isString(type) -> decoder.decodeString()
            MsgPackType.Bin.isBinary(type) -> decoder.decodeSerializableValue(ByteArraySerializer())
            MsgPackType.Array.isArray(type) -> ListSerializer(this).deserialize(decoder)
            MsgPackType.Map.isMap(type) -> MapSerializer(this, this).deserialize(decoder)
            MsgPackType.Ext.isExt(type) -> dynamicMsgPackExtensionSerializer.deserialize(decoder)
            else -> throw MsgPackSerializationException.dynamicSerializationError("Missing decoder for type: $type")
        }
    }

    @OptIn(InternalSerializationApi::class)
    final override val descriptor: SerialDescriptor = buildSerialDescriptor("MsgPackNullableDynamic", SerialKind.CONTEXTUAL)

    @OptIn(InternalSerializationApi::class)
    final override fun serialize(
        encoder: Encoder,
        value: Any?,
    ) {
        // Use ::class matching for numeric types instead of `is` checks because on Kotlin/JS
        // all numbers are JavaScript doubles, and `is Byte` matches any integer in byte range
        // regardless of the declared Kotlin type, causing incorrect encoding.
        when {
            value == null -> encoder.encodeNull()
            value is Boolean -> encoder.encodeBoolean(value)
            value is String -> encoder.encodeString(value)
            value is ByteArray -> encoder.encodeSerializableValue(ByteArraySerializer(), value)
            value is Map<*, *> -> MapSerializer(this, this).serialize(encoder, value as Map<Any?, Any?>)
            value is Array<*> -> ArraySerializer(this).serialize(encoder, value as Array<Any?>)
            value is List<*> -> ListSerializer(this).serialize(encoder, value)
            value is Map.Entry<*, *> -> MapEntrySerializer(this, this).serialize(encoder, value)
            value::class == Byte::class -> encoder.encodeByte(value as Byte)
            value::class == Short::class -> encoder.encodeShort(value as Short)
            value::class == Int::class -> encoder.encodeInt(value as Int)
            value::class == Long::class -> encoder.encodeLong(value as Long)
            value::class == Float::class -> encoder.encodeFloat(value as Float)
            value::class == Double::class -> encoder.encodeDouble(value as Double)
            dynamicMsgPackExtensionSerializer.canSerialize(value) -> dynamicMsgPackExtensionSerializer.serialize(encoder, value)
            else -> encoder.encodeSerializableValue(value::class.serializer() as KSerializer<Any>, value)
        }
    }
}
