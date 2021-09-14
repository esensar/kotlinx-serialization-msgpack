package com.ensarsarajcic.kotlinx.serialization.msgpack.extensions

import kotlin.reflect.KClass

sealed class DynamicMsgPackExtensionSerializer : BaseMsgPackExtensionSerializer<Any?>() {
    companion object Default : DynamicMsgPackExtensionSerializer()
    private data class SerializerPair<T : Any>(val klass: KClass<T>, val serializer: BaseMsgPackExtensionSerializer<T>) {
        fun deserialize(extension: MsgPackExtension): T {
            return serializer.deserialize(extension)
        }

        fun serialize(value: Any?): MsgPackExtension {
            return serializer.serialize(value as T)
        }
    }

    private val serializers = HashMap<Byte, SerializerPair<*>>()

    inline fun <reified T : Any> register(serializer: BaseMsgPackExtensionSerializer<T>) {
        register(serializer, T::class)
    }

    fun <T : Any> register(serializer: BaseMsgPackExtensionSerializer<T>, klass: KClass<T>) {
        serializers[serializer.extTypeId] = SerializerPair(klass, serializer)
    }

    fun unregister(serializer: BaseMsgPackExtensionSerializer<*>) {
        serializers.remove(serializer.extTypeId)
    }

    fun canSerialize(value: Any?): Boolean {
        if (value is MsgPackExtension) return true
        for (serializer in serializers.values) {
            if (serializer.klass.isInstance(value)) {
                return true
            }
        }
        return false
    }

    override fun deserialize(extension: MsgPackExtension): Any? {
        val pair = serializers[extension.extTypeId] ?: return extension
        return pair.deserialize(extension)
    }

    override fun serialize(extension: Any?): MsgPackExtension {
        if (extension is MsgPackExtension) {
            return extension
        }
        for (serializer in serializers.values) {
            if (serializer.klass.isInstance(extension)) {
                return serializer.serialize(extension)
            }
        }
        throw TODO("missing serializer")
    }

    override val extTypeId: Byte = 0x00
    override val checkTypeId: Boolean = false
}
