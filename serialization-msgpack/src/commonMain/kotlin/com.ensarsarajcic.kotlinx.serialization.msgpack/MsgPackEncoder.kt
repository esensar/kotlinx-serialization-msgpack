package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.types.MsgPackType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

internal class MsgPackEncoder(
    private val configuration: MsgPackConfiguration,
    override val serializersModule: SerializersModule
) : AbstractEncoder() {
    // TODO this may not be a list in the end
    //      It sould probably be delegated to something
    val result = mutableListOf<Byte>()

    override fun encodeBoolean(value: Boolean) {
        result.add(MsgPackType.Boolean(value))
    }

    override fun encodeNull() {
        result.add(MsgPackType.NULL)
    }

    override fun encodeByte(value: Byte) {
        if (value >= MsgPackType.Int.MIN_NEGATIVE_SINGLE_BYTE) {
            result.add(value)
        } else {
            result.add(MsgPackType.Int.INT8)
            result.add(value)
        }
    }

    override fun encodeShort(value: Short) {
        if (value in MsgPackType.Int.MIN_NEGATIVE_BYTE..Byte.MAX_VALUE) {
            encodeByte(value.toByte())
        } else {
            var uByte = false
            result.add(
                when {
                    value < 0 -> MsgPackType.Int.INT16
                    value <= MsgPackType.Int.MAX_UBYTE -> MsgPackType.Int.UINT8.also { uByte = true }
                    else -> MsgPackType.Int.UINT16
                }
            )
            if (uByte) {
                result.add((value.toInt() and 0xff).toByte())
            } else {
                result.addAll(value.splitToByteArray().toList())
            }
        }
    }

    override fun encodeInt(value: Int) {
        if (value in Short.MIN_VALUE..Short.MAX_VALUE) {
            encodeShort(value.toShort())
        } else {
            var uShort = false
            result.add(
                when {
                    value < 0 -> MsgPackType.Int.INT32
                    value <= MsgPackType.Int.MAX_USHORT -> MsgPackType.Int.UINT16.also { uShort = true }
                    else -> MsgPackType.Int.UINT32
                }
            )
            if (uShort) {
                result.addAll(value.toShort().splitToByteArray().toList())
            } else {
                result.addAll(value.splitToByteArray().toList())
            }
        }
    }

    override fun encodeLong(value: Long) {
        if (value in Int.MIN_VALUE..Int.MAX_VALUE) {
            encodeInt(value.toInt())
        } else {
            var uInt = false
            result.add(
                when {
                    value < 0 -> MsgPackType.Int.INT64
                    value <= MsgPackType.Int.MAX_UINT -> MsgPackType.Int.UINT32.also { uInt = true }
                    else -> MsgPackType.Int.UINT64
                }
            )
            if (uInt) {
                result.addAll(value.toInt().splitToByteArray().toList())
            } else {
                result.addAll(value.splitToByteArray().toList())
            }
        }
    }

    override fun encodeFloat(value: Float) {
        result.add(MsgPackType.Float.FLOAT)
        result.addAll(value.toRawBits().splitToByteArray().toList())
    }

    override fun encodeDouble(value: Double) {
        result.add(MsgPackType.Float.DOUBLE)
        result.addAll(value.toRawBits().splitToByteArray().toList())
    }

    override fun encodeString(value: String) {
        val bytes = value.encodeToByteArray()
        when {
            bytes.size <= MsgPackType.String.MAX_FIXSTR_LENGTH -> {
                result.add(MsgPackType.String.FIXSTR_SIZE_MASK.maskValue(bytes.size.toByte()))
            }
            bytes.size <= MsgPackType.String.MAX_STR8_LENGTH -> {
                result.add(MsgPackType.String.STR8)
                result.addAll(bytes.size.toByte().splitToByteArray().toList())
            }
            bytes.size <= MsgPackType.String.MAX_STR16_LENGTH -> {
                result.add(MsgPackType.String.STR16)
                result.addAll(bytes.size.toShort().splitToByteArray().toList())
            }
            bytes.size <= MsgPackType.String.MAX_STR32_LENGTH -> {
                result.add(MsgPackType.String.STR32)
                result.addAll(bytes.size.toInt().splitToByteArray().toList())
            }
            else -> TODO("TOO LONG STRING")
        }
        result.addAll(bytes.toList())
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return if (descriptor.kind in arrayOf(StructureKind.CLASS, StructureKind.OBJECT)) {
            beginCollection(descriptor, descriptor.elementsCount)
            MsgPackClassEncoder()
        } else {
            this
        }
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        when (descriptor.kind) {
            StructureKind.LIST ->
                when {
                    collectionSize <= MsgPackType.Array.MAX_FIXARRAY_SIZE -> {
                        result.add(MsgPackType.Array.FIXARRAY_SIZE_MASK.maskValue(collectionSize.toByte()))
                    }
                    collectionSize <= MsgPackType.Array.MAX_ARRAY16_LENGTH -> {
                        result.add(MsgPackType.Array.ARRAY16)
                        result.addAll(collectionSize.toShort().splitToByteArray().toList())
                    }
                    collectionSize <= MsgPackType.Array.MAX_ARRAY32_LENGTH -> {
                        result.add(MsgPackType.Array.ARRAY32)
                        result.addAll(collectionSize.toInt().splitToByteArray().toList())
                    }
                    else -> TODO("TOO LONG COLLECTION")
                }

            StructureKind.CLASS, StructureKind.OBJECT, StructureKind.MAP ->
                when {
                    collectionSize <= MsgPackType.Map.MAX_FIXMAP_SIZE -> {
                        result.add(MsgPackType.Map.FIXMAP_SIZE_MASK.maskValue(collectionSize.toByte()))
                    }
                    collectionSize <= MsgPackType.Map.MAX_MAP16_LENGTH -> {
                        result.add(MsgPackType.Map.MAP16)
                        result.addAll(collectionSize.toShort().splitToByteArray().toList())
                    }
                    collectionSize <= MsgPackType.Map.MAX_MAP32_LENGTH -> {
                        result.add(MsgPackType.Map.MAP32)
                        result.addAll(collectionSize.toInt().splitToByteArray().toList())
                    }
                    else -> TODO("TOO LONG COLLECTION")
                }

            else -> TODO("UNSUPPORTED COLLECTION!")
        }
        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        // no-op, everything is handled when starting structure/collection
    }

    // TODO Refactor as a completely separate class
    internal inner class MsgPackClassEncoder : CompositeEncoder {
        override val serializersModule: SerializersModule = this@MsgPackEncoder.serializersModule

        private fun encodeName(descriptor: SerialDescriptor, index: Int) {
            encodeString(descriptor.getElementName(index))
        }

        override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
            encodeName(descriptor, index)
            encodeBoolean(value)
        }

        override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
            encodeName(descriptor, index)
            encodeByte(value)
        }

        override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
            encodeName(descriptor, index)
            encodeChar(value)
        }

        override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
            encodeName(descriptor, index)
            encodeDouble(value)
        }

        override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
            encodeName(descriptor, index)
            encodeFloat(value)
        }

        override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
            encodeName(descriptor, index)
            encodeInt(value)
        }

        override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
            encodeName(descriptor, index)
            encodeLong(value)
        }

        override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
            encodeName(descriptor, index)
            encodeShort(value)
        }

        override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
            encodeName(descriptor, index)
            encodeString(value)
        }

        override fun endStructure(descriptor: SerialDescriptor) {
            // No-op
        }

        @ExperimentalSerializationApi
        override fun <T : Any> encodeNullableSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T?
        ) {
            encodeName(descriptor, index)
            encodeNullableSerializableValue(serializer, value)
        }

        override fun <T> encodeSerializableElement(
            descriptor: SerialDescriptor,
            index: Int,
            serializer: SerializationStrategy<T>,
            value: T
        ) {
            encodeName(descriptor, index)
            encodeSerializableValue(serializer, value)
        }
    }

    private inline fun <reified T : Number> T.splitToByteArray(): ByteArray {
        val byteCount = when (T::class) {
            Byte::class -> 1
            Short::class -> 2
            Int::class -> 4
            Long::class -> 8
            else -> throw UnsupportedOperationException("Can't split number of type ${T::class} to bytes!")
        }

        val result = ByteArray(byteCount)
        (byteCount - 1).downTo(0).forEach {
            result[byteCount - (it + 1)] = ((this.toLong() shr (8 * it)) and 0xff).toByte()
        }
        return result
    }
}
