package com.ensarsarajcic.kotlinx.serialization.msgpack.internal

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPackConfiguration
import com.ensarsarajcic.kotlinx.serialization.msgpack.stream.MsgPackDataOutputBuffer
import com.ensarsarajcic.kotlinx.serialization.msgpack.types.MsgPackType
import com.ensarsarajcic.kotlinx.serialization.msgpack.utils.splitToByteArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

internal class BasicMsgPackEncoder(
    private val configuration: MsgPackConfiguration,
    override val serializersModule: SerializersModule,
    private val packer: MsgPacker = BasicMsgPacker()
) : AbstractEncoder() {
    val result = MsgPackDataOutputBuffer()

    override fun encodeBoolean(value: Boolean) {
        result.addAll(packer.packBoolean(value))
    }

    override fun encodeNull() {
        result.addAll(packer.packNull())
    }

    override fun encodeByte(value: Byte) {
        result.addAll(packer.packByte(value))
    }

    override fun encodeShort(value: Short) {
        result.addAll(packer.packShort(value))
    }

    override fun encodeInt(value: Int) {
        result.addAll(packer.packInt(value))
    }

    override fun encodeLong(value: Long) {
        result.addAll(packer.packLong(value))
    }

    override fun encodeFloat(value: Float) {
        result.addAll(packer.packFloat(value))
    }

    override fun encodeDouble(value: Double) {
        result.addAll(packer.packDouble(value))
    }

    override fun encodeString(value: String) {
        result.addAll(packer.packString(value, configuration.rawCompatibility))
    }

    fun encodeByteArray(value: ByteArray) {
        if (configuration.rawCompatibility) {
            result.addAll(packer.packString(value.decodeToString(), true))
        } else {
            result.addAll(packer.packByteArray(value))
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return if (descriptor.kind in arrayOf(StructureKind.CLASS, StructureKind.OBJECT)) {
            if (descriptor.serialName == "com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackExtension") {
                ExtensionTypeEncoder(this)
            } else {
                beginCollection(descriptor, descriptor.elementsCount)
                MsgPackClassEncoder(this)
            }
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

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        if (serializer == ByteArraySerializer()) {
            encodeByteArray(value as ByteArray)
        } else {
            println("Serializing $value with $serializer")
            super.encodeSerializableValue(serializer, value)
        }
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        // no-op, everything is handled when starting structure/collection
    }
}

internal class MsgPackEncoder(
    private val basicMsgPackEncoder: BasicMsgPackEncoder
) : Encoder by basicMsgPackEncoder, CompositeEncoder by basicMsgPackEncoder {
    override val serializersModule: SerializersModule = basicMsgPackEncoder.serializersModule
    val result = basicMsgPackEncoder.result
}

internal class ExtensionTypeEncoder(
    private val basicMsgPackEncoder: BasicMsgPackEncoder
) : AbstractEncoder() {
    override val serializersModule: SerializersModule = basicMsgPackEncoder.serializersModule
    val result = basicMsgPackEncoder.result

    // TODO refactor
    private var bytesWritten = 0
    private var type: Byte? = null
    private var size: Int? = null
    private var typeId: Byte? = null

    override fun encodeByte(value: Byte) {
        if (bytesWritten == 0) {
            result.add(value)
            type = value
        } else if (bytesWritten == 1) {
            if (MsgPackType.Ext.SIZES.containsKey(type)) {
                result.add(value)
                size = MsgPackType.Ext.SIZES[type]
            }
            typeId = value
        }
        bytesWritten += 1
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        val value = value as ByteArray
        if (size == null) {
            size = value.size
            val maxSize = when (type) {
                MsgPackType.Ext.EXT8 -> MsgPackType.Ext.MAX_EXT8_LENGTH
                MsgPackType.Ext.EXT16 -> MsgPackType.Ext.MAX_EXT16_LENGTH
                MsgPackType.Ext.EXT32 -> MsgPackType.Ext.MAX_EXT32_LENGTH
                else -> TODO("HANDLE")
            }.toLong()
            if (size!!.toLong() > maxSize) throw TODO("Size exceeded")
            result.addAll(
                when (type) {
                    MsgPackType.Ext.EXT8 -> size!!.toByte().splitToByteArray()
                    MsgPackType.Ext.EXT16 -> size!!.toShort().splitToByteArray()
                    MsgPackType.Ext.EXT32 -> size!!.toInt().splitToByteArray()
                    else -> TODO("HANDLE")
                }
            )
            result.add(typeId!!)
        } else {
            if (value.size != size) throw TODO("Invalid size")
        }
        result.addAll(value)
    }
}

internal class MsgPackClassEncoder(
    private val basicMsgPackEncoder: BasicMsgPackEncoder
) : Encoder by basicMsgPackEncoder, CompositeEncoder by basicMsgPackEncoder {
    override val serializersModule: SerializersModule = basicMsgPackEncoder.serializersModule
    val result = basicMsgPackEncoder.result

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

    @ExperimentalSerializationApi
    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        return basicMsgPackEncoder
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
