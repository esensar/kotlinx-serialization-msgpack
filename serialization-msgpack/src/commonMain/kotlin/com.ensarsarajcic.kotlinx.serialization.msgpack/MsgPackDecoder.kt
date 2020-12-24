package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.types.MsgPackType
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule

internal class MsgPackDecoder(
    private val configuration: MsgPackConfiguration,
    override val serializersModule: SerializersModule,
    private val byteArray: ByteArray
) : AbstractDecoder() {
    // TODO extract into some form of ByteStream
    private var index = 0
    private fun nextByteOrNull(): Byte? = byteArray.getOrNull(index++)
    private fun requireNextByte(): Byte = nextByteOrNull() ?: throw Exception("End of stream")
    private fun takeNext(next: Int): ByteArray {
        require(next > 0) { "Number of bytes to take must be greater than 0!" }
        val result = ByteArray(next)
        (0 until next).forEach {
            result[it] = requireNextByte()
        }
        return result
    }

    // TODO Don't use flags, separate composite decoders for classes, lists and maps
    private var decodingClass = false

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (descriptor.kind in arrayOf(StructureKind.CLASS, StructureKind.OBJECT)) {
            // TODO Improve structure end logic
            //      This will probably fail in nested structures
            val fieldName = kotlin.runCatching { decodeString() }.getOrNull() ?: return CompositeDecoder.DECODE_DONE
            return descriptor.getElementIndex(fieldName)
        }
        return 0
    }

    override fun decodeSequentially(): Boolean = !decodingClass

    override fun decodeNotNullMark(): Boolean {
        val next = byteArray.getOrNull(index) ?: throw Exception("End of stream")
        return next != MsgPackType.NULL
    }

    override fun decodeNull(): Nothing? {
        val next = requireNextByte()
        return if (next == MsgPackType.NULL) null else throw Exception("Invalid null $next")
    }

    override fun decodeBoolean(): Boolean {
        return when (val next = requireNextByte()) {
            MsgPackType.Boolean.TRUE -> true
            MsgPackType.Boolean.FALSE -> false
            else -> throw Exception("Invalid boolean $next")
        }
    }

    override fun decodeByte(): Byte {
        // Check is it a single byte value
        val next = requireNextByte()
        return when {
            MsgPackType.Int.POSITIVE_FIXNUM_MASK.test(next) or MsgPackType.Int.NEGATIVE_FIXNUM_MASK.test(next) -> next
            // TODO reader is not handling overflows (when using unsigned types)
            MsgPackType.Int.isByte(next) -> nextByteOrNull() ?: throw Exception("End of stream")
            else -> throw TODO("Add a more descriptive error when wrong type is found!")
        }
    }

    override fun decodeShort(): Short {
        val next = byteArray.getOrNull(index) ?: throw Exception("End of stream")
        return when {
            MsgPackType.Int.isShort(next) -> {
                index++
                takeNext(2).joinToNumber()
            }
            next == MsgPackType.Int.UINT8 -> {
                index++
                (requireNextByte().toInt() and 0xff).toShort()
            }
            else -> decodeByte().toShort()
        }
    }

    override fun decodeInt(): Int {
        val next = byteArray.getOrNull(index) ?: throw Exception("End of stream")
        return when {
            MsgPackType.Int.isInt(next) -> {
                index++
                takeNext(4).joinToNumber()
            }
            next == MsgPackType.Int.UINT16 -> {
                index++
                takeNext(2).joinToNumber()
            }
            else -> decodeShort().toInt()
        }
    }

    override fun decodeLong(): Long {
        val next = byteArray.getOrNull(index) ?: throw Exception("End of stream")
        return when {
            MsgPackType.Int.isLong(next) -> {
                index++
                takeNext(8).joinToNumber()
            }
            next == MsgPackType.Int.UINT32 -> {
                index++
                takeNext(4).joinToNumber()
            }
            else -> decodeInt().toLong()
        }
    }

    override fun decodeFloat(): Float {
        val next = byteArray.getOrNull(index) ?: throw Exception("End of stream")
        return when (next) {
            MsgPackType.Float.FLOAT -> {
                index++
                Float.fromBits(takeNext(4).joinToNumber())
            }
            else -> TODO("Add a more descriptive error when wrong type is found!")
        }
    }

    override fun decodeDouble(): Double {
        val next = byteArray.getOrNull(index) ?: throw Exception("End of stream")
        return when (next) {
            MsgPackType.Float.DOUBLE -> {
                index++
                Double.fromBits(takeNext(8).joinToNumber())
            }
            MsgPackType.Float.FLOAT -> decodeFloat().toDouble()
            else -> TODO("Add a more descriptive error when wrong type is found!")
        }
    }

    override fun decodeString(): String {
        val next = byteArray.getOrNull(index) ?: throw Exception("End of stream")
        index++
        val length = when {
            MsgPackType.String.FIXSTR_SIZE_MASK.test(next) -> MsgPackType.String.FIXSTR_SIZE_MASK.unMaskValue(next).toInt()
            next == MsgPackType.String.STR8 -> requireNextByte().toInt() and 0xff
            next == MsgPackType.String.STR16 -> takeNext(2).joinToNumber()
            // TODO: this may have issues with long strings, since size will overflow
            next == MsgPackType.String.STR32 -> takeNext(4).joinToNumber()
            else -> {
                index--
                throw TODO("Add a more descriptive error when wrong type is found!")
            }
        }
        if (length == 0) return ""
        return takeNext(length).decodeToString()
    }

    fun decodeByteArray(): ByteArray {
        val next = byteArray.getOrNull(index) ?: throw Exception("End of stream")
        index++
        val length = when (next) {
            MsgPackType.Bin.BIN8 -> requireNextByte().toInt() and 0xff
            MsgPackType.Bin.BIN16 -> takeNext(2).joinToNumber()
            // TODO: this may have issues with long byte arrays, since size will overflow
            MsgPackType.Bin.BIN32 -> takeNext(4).joinToNumber()
            else -> {
                index--
                throw TODO("Add a more descriptive error when wrong type is found!")
            }
        }
        if (length == 0) return byteArrayOf()
        return takeNext(length)
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        val next = byteArray.getOrNull(index) ?: throw Exception("End of stream")
        index++

        return when (descriptor.kind) {
            StructureKind.LIST ->
                when {
                    MsgPackType.Array.FIXARRAY_SIZE_MASK.test(next) -> MsgPackType.Array.FIXARRAY_SIZE_MASK.unMaskValue(next).toInt()
                    next == MsgPackType.Array.ARRAY16 -> takeNext(2).joinToNumber()
                    // TODO: this may have issues with long arrays, since size will overflow
                    next == MsgPackType.Array.ARRAY32 -> takeNext(4).joinToNumber()
                    else -> {
                        index--
                        throw TODO("Add a more descriptive error when wrong type is found!")
                    }
                }

            StructureKind.CLASS, StructureKind.OBJECT, StructureKind.MAP ->
                when {
                    MsgPackType.Map.FIXMAP_SIZE_MASK.test(next) -> MsgPackType.Map.FIXMAP_SIZE_MASK.unMaskValue(next).toInt()
                    next == MsgPackType.Map.MAP16 -> takeNext(2).joinToNumber()
                    // TODO: this may have issues with long objects, since size will overflow
                    next == MsgPackType.Map.MAP16 -> takeNext(4).joinToNumber()
                    else -> {
                        index--
                        throw TODO("Add a more descriptive error when wrong type is found!")
                    }
                }

            else -> {
                index--
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
            decodingClass = true
            decodeCollectionSize(descriptor)
        }
        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        decodingClass = false
        super.endStructure(descriptor)
    }

    private inline fun <reified T : Number> ByteArray.joinToNumber(): T {
        val number = mapIndexed { index, byte ->
            (byte.toLong() and 0xff) shl (8 * (size - (index + 1)))
        }.fold(0L) { acc, it ->
            acc or it
        }
        return when (T::class) {
            Byte::class -> number.toByte()
            Short::class -> number.toShort()
            Int::class -> number.toInt()
            Long::class -> number
            else -> throw UnsupportedOperationException("Can't build ${T::class} from ByteArray (${this.toList()})")
        } as T
    }
}
