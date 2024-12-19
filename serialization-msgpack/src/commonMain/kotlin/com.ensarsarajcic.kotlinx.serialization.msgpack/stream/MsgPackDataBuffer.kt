package com.ensarsarajcic.kotlinx.serialization.msgpack.stream

interface MsgPackDataBuffer {
    fun toByteArray(): ByteArray
}

class MsgPackDataOutputBuffer() : MsgPackDataBuffer {
    private val byteArrays = mutableListOf<ByteArray>()

    fun add(byte: Byte) {
        byteArrays.add(ByteArray(1) { byte })
    }

    fun addAll(bytes: List<Byte>) {
        if (bytes.isNotEmpty()) {
            byteArrays.add(bytes.toByteArray())
        }
    }

    fun addAll(bytes: ByteArray) {
        if (bytes.isNotEmpty()) {
            byteArrays.add(bytes)
        }
    }

    override fun toByteArray(): ByteArray {
        val totalSize = byteArrays.sumOf { it.size }
        val outputArray = ByteArray(totalSize)
        var currentIndex = 0
        byteArrays.forEach {
            it.copyInto(outputArray, currentIndex)
            currentIndex += it.size
        }
        return outputArray
    }
}

class MsgPackDataInputBuffer(private val byteArray: ByteArray) : MsgPackDataBuffer {
    var index = 0
        private set

    fun skip(bytes: Int) {
        index += bytes
    }

    fun peek(): Byte = byteArray.getOrNull(index) ?: throw Exception("End of stream")

    fun peekSafely(): Byte? = byteArray.getOrNull(index)

    // Increases index only if next byte is not null
    fun nextByteOrNull(): Byte? = byteArray.getOrNull(index)?.also { index++ }

    fun requireNextByte(): Byte = nextByteOrNull() ?: throw Exception("End of stream")

    fun takeNext(next: Int): ByteArray {
        require(next > 0) { "Number of bytes to take must be greater than 0!" }
        val result = ByteArray(next)
        (0 until next).forEach {
            result[it] = requireNextByte()
        }
        return result
    }

    override fun toByteArray() = byteArray
}

internal fun ByteArray.toMsgPackBuffer() = MsgPackDataInputBuffer(this)
