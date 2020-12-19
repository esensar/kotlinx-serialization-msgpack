package com.ensarsarajcic.kotlinx.serialization.msgpack

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
    val floatTestPairs = arrayOf(
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
    val intArrayTestPairs = arrayOf(
        "93010203" to arrayOf(1, 2, 3),
        "94ffccffcd0145ce0009fbf4" to arrayOf(-1, 255, 325, 654324),
    )
    val stringArrayTestPairs = arrayOf(
        "92a3616263a3646566" to arrayOf("abc", "def"),
        "92d921616266647366736466736466647366736466736673646673647364666466646663a3616473" to arrayOf("abfdsfsdfsdfdsfsdfsfsdfsdsdfdfdfc", "ads")
    )
    val mixedListTestPairs = arrayOf(
        "92a3616263a3646566" to listOf("abc", 1, 325, 0.12f)
    )
}
