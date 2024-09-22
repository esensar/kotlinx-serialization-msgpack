package com.ensarsarajcic.kotlinx.serialization.msgpack.datetime

import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackExtension
import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackTimestamp
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.fail

class MsgPackDatetimeSerializerTest {
    private val t32TestPairs =
        arrayOf(
            Instant.fromEpochSeconds(0) to
                MsgPackExtension(
                    MsgPackExtension.Type.FIXEXT4,
                    -1,
                    byteArrayOf(0x00, 0x00, 0x00, 0x00),
                ),
            Instant.fromEpochSeconds(1000) to
                MsgPackExtension(
                    MsgPackExtension.Type.FIXEXT4,
                    -1,
                    byteArrayOf(0x00, 0x00, 0x03, 0xe8.toByte()),
                ),
            Instant.fromEpochSeconds(4_294_967_295) to
                MsgPackExtension(
                    MsgPackExtension.Type.FIXEXT4, -1,
                    byteArrayOf(
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte(),
                    ),
                ),
        )

    private val t64TestPairs =
        arrayOf(
            Instant.fromEpochSeconds(0, 999_999_999) to
                MsgPackExtension(
                    MsgPackExtension.Type.FIXEXT8,
                    -1,
                    byteArrayOf(
                        0xee.toByte(), 0x6b, 0x27, 0xfc.toByte(),
                        0x00, 0x00, 0x00, 0x00,
                    ),
                ),
            Instant.fromEpochSeconds(50000, 1000) to
                MsgPackExtension(
                    MsgPackExtension.Type.FIXEXT8,
                    -1,
                    byteArrayOf(
                        0x00, 0x00, 0x0f, 0xa0.toByte(),
                        0x00, 0x00, 0xc3.toByte(), 0x50,
                    ),
                ),
        )

    private val t92TestPairs =
        arrayOf(
            Instant.fromEpochSeconds(-1000) to
                MsgPackExtension(
                    MsgPackExtension.Type.EXT8,
                    -1,
                    byteArrayOf(
                        0x00, 0x00, 0x00, 0x00,
                        0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte(),
                        0xff.toByte(), 0xff.toByte(), 0xfc.toByte(), 0x18,
                    ),
                ),
            Instant.fromEpochSeconds(-1000, 1000) to
                MsgPackExtension(
                    MsgPackExtension.Type.EXT8,
                    -1,
                    byteArrayOf(
                        0x00, 0x00, 0x03, 0xe8.toByte(),
                        0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte(),
                        0xff.toByte(), 0xff.toByte(), 0xfc.toByte(), 0x18,
                    ),
                ),
            Instant.fromEpochSeconds(Instant.DISTANT_FUTURE.epochSeconds) to
                MsgPackExtension(
                    MsgPackExtension.Type.EXT8,
                    -1,
                    byteArrayOf(
                        0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x02, 0xd0.toByte(),
                        0x44, 0xa2.toByte(), 0xeb.toByte(), 0x00,
                    ),
                ),
            Instant.fromEpochSeconds(Instant.DISTANT_PAST.epochSeconds) to
                MsgPackExtension(
                    MsgPackExtension.Type.EXT8,
                    -1,
                    byteArrayOf(
                        0x00, 0x00, 0x00, 0x00,
                        0xff.toByte(), 0xff.toByte(), 0xfd.toByte(), 0x12,
                        0xc8.toByte(), 0x74, 0x1c, 0xff.toByte(),
                    ),
                ),
        )

    @Test
    fun testT32Encode() {
        testEncodePairs<MsgPackTimestamp.T32>(t32TestPairs)
    }

    @Test
    fun testT32Decode() {
        testDecodePairs<MsgPackTimestamp.T32>(t32TestPairs)
    }

    @Test
    fun testT64Encode() {
        testEncodePairs<MsgPackTimestamp.T64>(t64TestPairs)
    }

    @Test
    fun testT64Decode() {
        testDecodePairs<MsgPackTimestamp.T64>(t64TestPairs)
    }

    @Test
    fun testT92Encode() {
        testEncodePairs<MsgPackTimestamp.T92>(t92TestPairs)
    }

    @Test
    fun testT92Decode() {
        testDecodePairs<MsgPackTimestamp.T92>(t92TestPairs)
    }

    private inline fun <reified T : MsgPackTimestamp> testEncodePairs(pairs: Array<Pair<Instant, MsgPackExtension>>) {
        pairs.forEach { (time, expected) ->
            val serializer =
                when (T::class) {
                    MsgPackTimestamp.T32::class -> MsgPackTimestamp32DatetimeSerializer()
                    MsgPackTimestamp.T64::class -> MsgPackTimestamp64DatetimeSerializer()
                    MsgPackTimestamp.T92::class -> MsgPackTimestamp92DatetimeSerializer()
                    else -> fail()
                }
            val result = serializer.serialize(time)
            assertEquals(expected.type, result.type)
            assertEquals(expected.extTypeId, result.extTypeId)
            assertContentEquals(expected.data, result.data)
        }
    }

    private inline fun <reified T : MsgPackTimestamp> testDecodePairs(pairs: Array<Pair<Instant, MsgPackExtension>>) {
        pairs.forEach { (expected, extension) ->
            val serializer =
                when (T::class) {
                    MsgPackTimestamp.T32::class -> MsgPackTimestamp32DatetimeSerializer()
                    MsgPackTimestamp.T64::class -> MsgPackTimestamp64DatetimeSerializer()
                    MsgPackTimestamp.T92::class -> MsgPackTimestamp92DatetimeSerializer()
                    else -> fail()
                }
            val result = serializer.deserialize(extension)
            assertEquals(expected, result)
        }
    }
}
