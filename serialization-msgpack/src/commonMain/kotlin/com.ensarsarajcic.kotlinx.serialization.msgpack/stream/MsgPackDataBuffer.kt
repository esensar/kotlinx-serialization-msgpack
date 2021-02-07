package com.ensarsarajcic.kotlinx.serialization.msgpack.stream

internal class MsgPackDataBuffer(
    private val byteArray: ByteArray
) {
    private var index = 0

    fun skip(bytes: Int) {
        index += bytes
    }

    fun peek(): Byte = byteArray.getOrNull(index) ?: throw Exception("End of stream")

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
}

internal fun ByteArray.toMsgPackBuffer() = MsgPackDataBuffer(this)