package com.ensarsarajcic.kotlinx.serialization.msgpack.unsigned

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
internal class UnsignedCodersTests {
    val uByteTestPairs =
        arrayOf(
            "ccff" to 255.toUByte(),
            "ccdc" to 220.toUByte(),
            "cc80" to 128.toUByte(),
        )
    val uShortTestPairs =
        arrayOf(
            "cdfde8" to 65000.toUShort(),
            "cdffff" to 65535.toUShort(),
            "cd8000" to 32768.toUShort(),
        )
    val uIntTestPairs =
        arrayOf(
            "ceffffffff" to 4294967295.toUInt(),
            "ceb2d05e00" to 3000000000.toUInt(),
            "ce80000000" to 2147483648.toUInt(),
        )
    val uLongTestPairs =
        arrayOf(
            "cfffffffffffffffff" to ULong.MAX_VALUE,
            "cf000462d53c987513" to 1234567891023123.toULong(),
            "cf8000000000000000" to Long.MAX_VALUE.toULong() + 1u,
            "cf0000000000000000" to ULong.MIN_VALUE,
        )

    @Test
    fun testUByteEncode() {
        testEncodePairs(
            UByte.serializer(),
            *uByteTestPairs,
        )
    }

    @Test
    fun testUByteDecode() {
        testDecodePairs(
            UByte.serializer(),
            *uByteTestPairs,
        )
    }

    @Test
    fun testUShortEncode() {
        testEncodePairs(
            UShort.serializer(),
            *uShortTestPairs,
        )
    }

    @Test
    fun testUShortDecode() {
        testDecodePairs(
            UShort.serializer(),
            *uShortTestPairs,
        )
    }

    @Test
    fun testUIntEncode() {
        testEncodePairs(
            UInt.serializer(),
            *uIntTestPairs,
        )
    }

    @Test
    fun testUIntDecode() {
        testDecodePairs(
            UInt.serializer(),
            *uIntTestPairs,
        )
    }

    @Test
    fun testULongEncode() {
        testEncodePairs(
            ULong.serializer(),
            *uLongTestPairs,
        )
    }

    @Test
    fun testULongDecode() {
        testDecodePairs(
            ULong.serializer(),
            *uLongTestPairs,
        )
    }

    private fun <T> testEncodePairs(
        serializer: KSerializer<T>,
        vararg pairs: Pair<String, T>,
    ) {
        pairs.forEach { (expectedResult, value) ->
            assertEquals(
                expectedResult,
                MsgPack(
                    inlineEncoders = MsgPackUnsignedSupport.ALL_ENCODERS,
                    inlineDecoders = MsgPackUnsignedSupport.ALL_DECODERS,
                ).encodeToByteArray(serializer, value).toHex(),
            )
        }
    }

    private fun <T> testDecodePairs(
        serializer: KSerializer<T>,
        vararg pairs: Pair<String, T>,
    ) {
        pairs.forEach { (value, expectedResult) ->
            assertEquals(
                expectedResult,
                MsgPack(
                    inlineEncoders = MsgPackUnsignedSupport.ALL_ENCODERS,
                    inlineDecoders = MsgPackUnsignedSupport.ALL_DECODERS,
                ).decodeFromByteArray(serializer, value.hexStringToByteArray()),
            )
        }
    }
}
