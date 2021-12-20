package com.ensarsarajcic.kotlinx.serialization.msgpack.internal

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPackConfiguration
import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPackNullableDynamicSerializer
import com.ensarsarajcic.kotlinx.serialization.msgpack.stream.MsgPackDataInputBuffer
import com.ensarsarajcic.kotlinx.serialization.msgpack.types.MsgPackType
import com.ensarsarajcic.kotlinx.serialization.msgpack.utils.joinToNumber
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

interface MsgPackTypeDecoder {
    fun peekNextType(): Byte
}

internal class BasicMsgPackDecoder(
    private val configuration: MsgPackConfiguration,
    override val serializersModule: SerializersModule,
    val dataBuffer: MsgPackDataInputBuffer,
    private val msgUnpacker: MsgUnpacker = BasicMsgUnpacker(dataBuffer),
    val inlineDecoders: Map<SerialDescriptor, (InlineDecoderHelper) -> Decoder> = mapOf()
) : AbstractDecoder(), MsgPackTypeDecoder {

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (descriptor.kind in arrayOf(StructureKind.CLASS, StructureKind.OBJECT)) {
            val next = dataBuffer.peekSafely()
            if (next != null && MsgPackType.String.isString(next)) {
                val fieldName = kotlin.runCatching { decodeString() }.getOrNull() ?: return CompositeDecoder.UNKNOWN_NAME
                val index = descriptor.getElementIndex(fieldName)
                return if (index == CompositeDecoder.UNKNOWN_NAME && configuration.ignoreUnknownKeys) {
                    MsgPackNullableDynamicSerializer.deserialize(this)
                    decodeElementIndex(descriptor)
                } else {
                    index
                }
            } else {
                return CompositeDecoder.DECODE_DONE
            }
        }
        return 0
    }

    override fun decodeSequentially(): Boolean = true

    override fun decodeNotNullMark(): Boolean {
        val next = dataBuffer.peek()
        return next != MsgPackType.NULL
    }

    override fun decodeNull(): Nothing? {
        msgUnpacker.unpackNull()
        return null
    }

    override fun decodeBoolean(): Boolean {
        return msgUnpacker.unpackBoolean()
    }

    override fun decodeByte(): Byte {
        return msgUnpacker.unpackByte(configuration.strictTypes, configuration.preventOverflows)
    }

    override fun decodeShort(): Short {
        return msgUnpacker.unpackShort(configuration.strictTypes, configuration.preventOverflows)
    }

    override fun decodeInt(): Int {
        return msgUnpacker.unpackInt(configuration.strictTypes, configuration.preventOverflows)
    }

    override fun decodeLong(): Long {
        return msgUnpacker.unpackLong(configuration.strictTypes, configuration.preventOverflows)
    }

    override fun decodeFloat(): Float {
        return msgUnpacker.unpackFloat(configuration.strictTypes)
    }

    override fun decodeDouble(): Double {
        return msgUnpacker.unpackDouble(configuration.strictTypes)
    }

    override fun decodeString(): String {
        return msgUnpacker.unpackString()
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return enumDescriptor.getElementIndex(decodeString())
    }

    fun decodeByteArray(): ByteArray {
        return if (configuration.rawCompatibility) {
            val next = dataBuffer.peek()
            if (MsgPackType.String.FIXSTR_SIZE_MASK.test(next) ||
                next == MsgPackType.String.STR16 ||
                next == MsgPackType.String.STR32
            ) {
                msgUnpacker.unpackString().encodeToByteArray()
            } else {
                msgUnpacker.unpackByteArray()
            }
        } else {
            msgUnpacker.unpackByteArray()
        }
    }

    override fun decodeInline(inlineDescriptor: SerialDescriptor): Decoder {
        if (inlineDecoders.containsKey(inlineDescriptor)) {
            return inlineDecoders[inlineDescriptor]!!(InlineDecoderHelper(serializersModule, dataBuffer))
        }
        return super.decodeInline(inlineDescriptor)
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        val next = dataBuffer.requireNextByte()
        return when (descriptor.kind) {
            StructureKind.LIST ->
                when {
                    MsgPackType.Array.FIXARRAY_SIZE_MASK.test(next) -> MsgPackType.Array.FIXARRAY_SIZE_MASK.unMaskValue(next).toInt()
                    next == MsgPackType.Array.ARRAY16 -> dataBuffer.takeNext(2).joinToNumber()
                    next == MsgPackType.Array.ARRAY32 -> {
                        if (configuration.preventOverflows) {
                            val number = dataBuffer.takeNext(4).joinToNumber<Long>()
                            if (number !in Int.MIN_VALUE..Int.MAX_VALUE) {
                                throw TODO("Overflow error")
                            } else {
                                number.toInt()
                            }
                        } else {
                            dataBuffer.takeNext(4).joinToNumber()
                        }
                    }
                    else -> {
                        throw TODO("Add a more descriptive error when wrong type is found!")
                    }
                }

            StructureKind.CLASS, StructureKind.OBJECT, StructureKind.MAP ->
                when {
                    MsgPackType.Map.FIXMAP_SIZE_MASK.test(next) -> MsgPackType.Map.FIXMAP_SIZE_MASK.unMaskValue(next).toInt()
                    next == MsgPackType.Map.MAP16 -> dataBuffer.takeNext(2).joinToNumber()
                    next == MsgPackType.Map.MAP32 -> {
                        if (configuration.preventOverflows) {
                            val number = dataBuffer.takeNext(4).joinToNumber<Long>()
                            if (number !in Int.MIN_VALUE..Int.MAX_VALUE) {
                                throw TODO("Overflow error")
                            } else {
                                number.toInt()
                            }
                        } else {
                            dataBuffer.takeNext(4).joinToNumber()
                        }
                    }
                    else -> {
                        throw TODO("Add a more descriptive error when wrong type is found!")
                    }
                }

            else -> {
                TODO("Unsupported collection")
            }
        }
    }

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        return if (deserializer == ByteArraySerializer()) {
            decodeByteArray() as T
        } else {
            super.decodeSerializableValue(deserializer)
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (descriptor.kind in arrayOf(StructureKind.CLASS, StructureKind.OBJECT)) {
            if (descriptor.serialName == "com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackExtension") {
                return ExtensionTypeDecoder(this)
            }
            // Handle extension types as arrays
            decodeCollectionSize(descriptor)
            return ClassMsgPackDecoder(this)
            // TODO compare with descriptor.elementsCount
        }
        return this
    }

    override fun peekNextType(): Byte {
        return dataBuffer.peek()
    }
}

internal class MsgPackDecoder(
    private val basicMsgPackDecoder: BasicMsgPackDecoder
) : Decoder by basicMsgPackDecoder, CompositeDecoder by basicMsgPackDecoder, MsgPackTypeDecoder by basicMsgPackDecoder {
    override val serializersModule: SerializersModule = basicMsgPackDecoder.serializersModule
}

internal class ClassMsgPackDecoder(
    private val basicMsgPackDecoder: BasicMsgPackDecoder
) : Decoder by basicMsgPackDecoder, CompositeDecoder by basicMsgPackDecoder, MsgPackTypeDecoder by basicMsgPackDecoder {
    override val serializersModule: SerializersModule = basicMsgPackDecoder.serializersModule

    private var decodedElements = 0

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (decodedElements >= descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
        val result = basicMsgPackDecoder.decodeElementIndex(descriptor)
        if (result != CompositeDecoder.DECODE_DONE) decodedElements++
        return result
    }

    override fun decodeSequentially(): Boolean = false
}

internal class ExtensionTypeDecoder(
    private val basicMsgPackDecoder: BasicMsgPackDecoder
) : CompositeDecoder, AbstractDecoder(), MsgPackTypeDecoder by basicMsgPackDecoder {
    private val dataBuffer = basicMsgPackDecoder.dataBuffer
    var type: Byte? = null
    var typeId: Byte? = null
    var size: Int? = null
    var bytesRead = 0

    override val serializersModule: SerializersModule = basicMsgPackDecoder.serializersModule

    override fun decodeSequentially(): Boolean = false

    override fun decodeByte(): Byte {
        return if (bytesRead == 0) {
            val byte = dataBuffer.requireNextByte()
            bytesRead++
            if (!MsgPackType.Ext.isExt(byte)) {
                throw TODO("Unexpected byte")
            }
            type = byte
            if (MsgPackType.Ext.SIZES.containsKey(type)) {
                size = MsgPackType.Ext.SIZES[type]
            }
            type!!
        } else if (bytesRead == 1 && size != null) {
            val byte = dataBuffer.requireNextByte()
            bytesRead++
            typeId = byte
            typeId!!
        } else if (bytesRead == 1 && size == null) {
            val sizeSize = MsgPackType.Ext.SIZE_SIZE[type]
            bytesRead += sizeSize!!
            size = dataBuffer.takeNext(sizeSize).joinToNumber()
            val byte = dataBuffer.requireNextByte()
            typeId = byte
            typeId!!
        } else {
            throw TODO("Handle?")
        }
    }
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return if (bytesRead <= 2) bytesRead else CompositeDecoder.DECODE_DONE
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        return size ?: 0
    }

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        bytesRead += 1
        return dataBuffer.takeNext(
            size!!
        ) as T
    }
}

data class InlineDecoderHelper(
    val serializersModule: SerializersModule,
    val inputBuffer: MsgPackDataInputBuffer
)
