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
        "ccdc" to 220
    )
    val shortTestPairs = arrayOf<Pair<String, Short>>(
        "cd7fff" to 32767,
        "d18001" to -32767,
        "d1fb2e" to -1234,
    )
    val uShortTestPairs = arrayOf(
        "cdfde8" to 65000,
        "cdffff" to 65535
    )
    val intTestPairs = arrayOf(
        "d2ffff0218" to -65000,
        "ce7fffffff" to 2147483647,
        "d280000001" to -2147483647
    )
}
