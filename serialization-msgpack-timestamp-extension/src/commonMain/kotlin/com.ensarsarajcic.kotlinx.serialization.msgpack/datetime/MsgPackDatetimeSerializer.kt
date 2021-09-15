package com.ensarsarajcic.kotlinx.serialization.msgpack.datetime

import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.BaseMsgPackExtensionSerializer
import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackExtension
import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackTimestamp
import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackTimestampExtensionSerializer
import kotlinx.datetime.Instant

sealed class BaseMsgPackDatetimeSerializer(private val outputType: Byte) : BaseMsgPackExtensionSerializer<Instant>() {
    private val timestampSerializer = MsgPackTimestampExtensionSerializer
    override fun deserialize(extension: MsgPackExtension): Instant {
        return when (val timestamp = timestampSerializer.deserialize(extension)) {
            is MsgPackTimestamp.T32 -> Instant.fromEpochSeconds(timestamp.seconds, 0)
            is MsgPackTimestamp.T64 -> Instant.fromEpochSeconds(timestamp.seconds, timestamp.nanoseconds)
            is MsgPackTimestamp.T92 -> Instant.fromEpochSeconds(timestamp.seconds, timestamp.nanoseconds)
        }
    }

    override fun serialize(extension: Instant): MsgPackExtension {
        val timestamp = when (outputType) {
            0.toByte() -> {
                MsgPackTimestamp.T32(extension.epochSeconds)
            }
            1.toByte() -> {
                MsgPackTimestamp.T64(extension.epochSeconds, extension.nanosecondsOfSecond)
            }
            2.toByte() -> {
                MsgPackTimestamp.T92(extension.epochSeconds, extension.nanosecondsOfSecond.toLong())
            }
            else -> TODO("Needs more info")
        }
        return timestampSerializer.serialize(timestamp)
    }

    override val extTypeId: Byte = -1
}

class MsgPackTimestamp32DatetimeSerializer() : BaseMsgPackDatetimeSerializer(0)
class MsgPackTimestamp64DatetimeSerializer() : BaseMsgPackDatetimeSerializer(1)
class MsgPackTimestamp92DatetimeSerializer() : BaseMsgPackDatetimeSerializer(2)
