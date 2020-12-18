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
}
