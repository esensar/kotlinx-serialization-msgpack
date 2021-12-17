package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.BaseMsgPackExtensionSerializer
import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackExtension
import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackTimestamp
import com.ensarsarajcic.kotlinx.serialization.msgpack.types.MsgPackType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object TestData {
    val booleanTestPairs = arrayOf(
        "c3" to true,
        "c2" to false
    )
    val byteTestPairs = arrayOf<Pair<String, Byte>>(
        "37" to 55,
        "e0" to -32,
        "7f" to 127,
        "00" to 0,
        "ff" to -1,
        "fe" to -2,
        "d0df" to -33,
        "d0ce" to -50,
        "d081" to -127,
        "32" to 50
    )
    val uByteTestPairs = arrayOf<Pair<String, Short>>(
        "ccff" to 255,
        "ccdc" to 220,
        "cc80" to 128
    )
    val shortTestPairs = arrayOf<Pair<String, Short>>(
        "cd7fff" to 32767,
        "cd0100" to 256,
        "d1ff80" to -128,
        "d1ff7f" to -129,
        "d18000" to -32768,
        "d1fb2e" to -1234,
    )
    val uShortTestPairs = arrayOf(
        "cdfde8" to 65000,
        "cdffff" to 65535,
        "cd8000" to 32768
    )
    val intTestPairs = arrayOf(
        "ce00010000" to 65536,
        "d2ffff0218" to -65000,
        "d2ffff7fff" to -32769,
        "ce7fffffff" to 2147483647,
        "d280000000" to -2147483648
    )
    val uIntTestPairs = arrayOf(
        "ceffffffff" to 4294967295,
        "ceb2d05e00" to 3000000000,
        "ce80000000" to 2147483648
    )
    val longTestPairs = arrayOf(
        "cf0000000100000000" to 4294967296,
        "cf000000012a05f200" to 5000000000,
        "cf7fffffffffffffff" to 9223372036854775807,
        "d3ffffffff7fffffff" to -2147483649,
        "d3fffffffed5fa0e00" to -5000000000
    )
    val floatTestPairs = arrayOf<Pair<String, Float>>(
        "ca3dfbe76d" to 0.123f,
        "cabdfbe76d" to -0.123f,
        "ca42f6e979" to 123.456f,
        "cac2f6e979" to -123.456f,
    )
    val doubleTestPairs = arrayOf(
        "cb3fbf7ced916872b0" to 0.123,
        "cbbfbf7ced916872b0" to -0.123,
        "cb405edd2f1a9fbe77" to 123.456,
        "cbc05edd2f1a9fbe77" to -123.456,
    )
    val fixStrTestPairs = arrayOf(
        "a0" to "",
        "a474657374" to "test",
        "a82d31323377686174" to "-123what",
        "a82d31323377686174" to "-123what",
        "bf74657374747474747474747474747474747474747474747474747474747474" to "testttttttttttttttttttttttttttt", // Max fixStr size
        "aac48dc487c48dc487c2bc" to "čćčć¼" // UTF-8 support
    )
    val str8TestPairs = arrayOf(
        "d9207465737474747474747474747474747474747474747474747474747474747474" to "testtttttttttttttttttttttttttttt", // Min Str8 size
        "d92a7465737474747474747474747474747474747474747474747474747474747474c48dc487c48dc487c2bc" to "testttttttttttttttttttttttttttttčćčć¼", // UTF-8 support
        "d9ff746573747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474" to "testttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt" // Max Str8 size
    )
    val str16TestPairs = arrayOf(
        "da010074657374747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474" to "testtttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt", // Min Str16 size
        "da010a74657374747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474c48dc487c48dc487c2bc" to "testttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttčćčć¼", // UTF-8 support
    )
    val bin8TestPairs = arrayOf(
        "c409010203040506070809" to byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
        "c400" to byteArrayOf(),
        "c4ff746573747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474747474" to byteArrayOf(116, 101, 115, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116, 116)
    )
    val intArrayTestPairs = arrayOf(
        "93010203" to arrayOf(1, 2, 3),
        "94ffccffcd0145ce0009fbf4" to arrayOf(-1, 255, 325, 654324),
    )
    val stringArrayTestPairs = arrayOf(
        "92a3616263a3646566" to arrayOf("abc", "def"),
        "92d921616266647366736466736466647366736466736673646673647364666466646663a3616473" to arrayOf("abfdsfsdfsdfdsfsdfsfsdfsdsdfdfdfc", "ads")
    )
    val mixedListTestPairs = arrayOf(
        "94a361626301cd0145cb3fbeb851eb851eb8" to listOf("abc", 1, 325, 0.12)
    )
    val mapTestPairs = arrayOf(
        "81a3616263a3646566" to mapOf("abc" to "def")
    )
    val timestampTestPairs = arrayOf(
        "d6ff00000000" to MsgPackTimestamp.T32(0),
        "d6ff000003e8" to MsgPackTimestamp.T32(1000),
        "d6ffffffffff" to MsgPackTimestamp.T32(4_294_967_295),
        "d7ffee6b27fc00000000" to MsgPackTimestamp.T64(0, 999_999_999),
        "d7ff00000fa00000c350" to MsgPackTimestamp.T64(50000, 1000),
        "c70cff00000000fffffffffffffc18" to MsgPackTimestamp.T92(-1000),
        "c70cff000003e8fffffffffffffc18" to MsgPackTimestamp.T92(-1000, 1000),
        "c70cff000000007fffffffffffffff" to MsgPackTimestamp.T92(9_223_372_036_854_775_807),
        "c70cff000000008000000000000001" to MsgPackTimestamp.T92(-9_223_372_036_854_775_807),
    )
    val strictWriteShortPairs = arrayOf<Pair<String, Short>>(
        "d1ffdf" to -33,
        "d1ffce" to -50,
        "d1ff81" to -127,
    )
    val strictWriteIntPairs = arrayOf<Pair<String, Int>>(
        "ce00007fff" to 32767,
        "ce00000100" to 256,
        "d2ffffff80" to -128,
        "d2ffffff7f" to -129,
    )
    val strictWriteLongPairs = arrayOf<Pair<String, Long>>(
        "cf0000000000007fff" to 32767,
        "cf0000000000000100" to 256,
        "d3ffffffffffffff80" to -128,
        "d3ffffffffffffff7f" to -129,
    )

    val enumTestPairs = arrayOf(
        "a44e4f4e45" to Vocation.NONE,
        "a54452554944" to Vocation.DRUID,
        "ab454c4445525f4452554944" to Vocation.ELDER_DRUID,
    )

    @Serializable
    data class SampleClass(
        @SerialName("testString")
        val testString: String,
        val testInt: Int,
        val testBoolean: Boolean
    )

    val sampleClassTestPairs = arrayOf(
        "83aa74657374537472696e67a3646566a774657374496e747bab74657374426f6f6c65616ec3" to SampleClass("def", 123, true)
    )
    val pairsTestPairs: Array<Pair<String, Pair<String, String>>> = arrayOf(
        "82a56669727374a5416c696365a67365636f6e64a3426f62" to Pair("Alice", "Bob")
    )
    val triplesTestPairs: Array<Pair<String, Triple<String, String, String>>> = arrayOf(
        "83a56669727374a5416c696365a67365636f6e64a3426f62a57468697264a454657374" to Triple("Alice", "Bob", "Test")
    )
}

@Serializable
// Taken from issue #63: https://github.com/esensar/kotlinx-serialization-msgpack/issues/63
enum class Vocation(val properName: String) {
    NONE("None"),
    DRUID("Druid"),
    SORCERER("Sorcerer"),
    PALADIN("Paladin"),
    KNIGHT("Knight"),
    ELDER_DRUID("Elder Druid"),
    MASTER_SORCERER("Master Sorcerer"),
    ROYAL_PALADIN("Royal Paladin"),
    ELITE_KNIGHT("Elite Knight");

    companion object {
        fun fromProperName(properName: String): Vocation? {
            return values().firstOrNull { it.properName.equals(properName, true) }
        }
    }
}

@Serializable(with = CustomExtensionSerializer::class)
data class CustomExtensionType(val data: List<Byte>)

class CustomExtensionSerializer() :
    BaseMsgPackExtensionSerializer<CustomExtensionType>() {
    override val extTypeId: Byte = 3

    override fun deserialize(extension: MsgPackExtension): CustomExtensionType = CustomExtensionType(extension.data.toList())

    override fun serialize(extension: CustomExtensionType): MsgPackExtension = MsgPackExtension(MsgPackType.Ext.FIXEXT2, extTypeId, extension.data.toByteArray())
}
