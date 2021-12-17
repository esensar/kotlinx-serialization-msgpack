package com.ensarsarajcic.kotlinx.serialization.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.DynamicMsgPackExtensionSerializer
import com.ensarsarajcic.kotlinx.serialization.msgpack.extensions.MsgPackTimestampExtensionSerializer
import kotlinx.serialization.KSerializer
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class MsgPackDynamicSerializerTest {

    @Test
    fun testDynamicDeserialize() {
        testDecodePairs(
            MsgPackDynamicSerializer,
            *(
                TestData.booleanTestPairs.toList() +
                    TestData.byteTestPairs.toList() +
                    TestData.uByteTestPairs.toList() +
                    TestData.shortTestPairs.toList() +
                    TestData.uShortTestPairs.toList() +
                    TestData.intTestPairs.toList() +
                    TestData.uIntTestPairs.toList() +
                    TestData.longTestPairs.toList() +
                    // Due to JS
//                    TestData.floatTestPairs.toList() +
                    TestData.doubleTestPairs.toList() +
                    TestData.fixStrTestPairs.toList() +
                    TestData.str8TestPairs.toList() +
                    TestData.str16TestPairs.toList() +
                    TestData.bin8TestPairs.toList() +
                    TestData.intArrayTestPairs.toList() +
                    TestData.stringArrayTestPairs.toList() +
                    TestData.mixedListTestPairs.toList() +
                    TestData.mapTestPairs.toList()
                ).toTypedArray()
        )
    }

    @Test
    fun testDynamicSerialize() {
        testEncodePairs(
            MsgPackDynamicSerializer,
            *(
                TestData.booleanTestPairs.toList() +
                    TestData.byteTestPairs.toList() +
                    TestData.uByteTestPairs.toList() +
                    TestData.shortTestPairs.toList() +
                    TestData.uShortTestPairs.toList() +
                    TestData.intTestPairs.toList() +
                    TestData.uIntTestPairs.toList() +
                    TestData.longTestPairs.toList() +
                    // Due to JS
//                    TestData.floatTestPairs.toList() +
                    TestData.doubleTestPairs.toList() +
                    TestData.fixStrTestPairs.toList() +
                    TestData.str8TestPairs.toList() +
                    TestData.str16TestPairs.toList() +
                    TestData.bin8TestPairs.toList() +
                    TestData.intArrayTestPairs.toList() +
                    TestData.stringArrayTestPairs.toList() +
                    TestData.mixedListTestPairs.toList() +
                    TestData.mapTestPairs.toList() +
                    TestData.enumTestPairs.toList()
                ).toTypedArray()
        )
    }

    @Test
    fun testDynamicDeserializeExtension() {
        val extensionSerializer = DynamicMsgPackExtensionSerializer()
        extensionSerializer.register(MsgPackTimestampExtensionSerializer)
        testDecodePairs(
            MsgPackDynamicSerializer(MsgPackNullableDynamicSerializer(extensionSerializer)),
            *(
                TestData.timestampTestPairs.toList()
                ).toTypedArray()
        )
    }

    @Test
    fun testDynamicSerializeExtension() {
        val extensionSerializer = DynamicMsgPackExtensionSerializer()
        extensionSerializer.register(MsgPackTimestampExtensionSerializer)
        testEncodePairs(
            MsgPackDynamicSerializer(MsgPackNullableDynamicSerializer(extensionSerializer)),
            *(
                TestData.timestampTestPairs.toList()
                ).toTypedArray()
        )
    }

    private fun <T> testEncodePairs(serializer: KSerializer<T>, vararg pairs: Pair<String, T>) {
        pairs.forEach { (expectedResult, value) ->
            val result = MsgPack.encodeToByteArray(serializer, value).toHex()
            assertEquals(expectedResult, result)
        }
    }
    private fun <T> testDecodePairs(serializer: KSerializer<T>, vararg pairs: Pair<String, T>) {
        pairs.forEach { (value, expectedResult) ->
            val result = MsgPack.decodeFromByteArray(serializer, value.hexStringToByteArray())
            when (expectedResult) {
                is ByteArray -> {
                    assertContentEquals(expectedResult, result as ByteArray)
                }
                is Array<*> -> {
                    assertContentEquals(
                        (expectedResult as Array<*>).map {
                            if (it is Number) it.toLong() else it
                        }.toList(),
                        (result as List<*>).map {
                            if (it is Number) it.toLong() else it
                        }
                    )
                }
                is List<*> -> {
                    assertContentEquals(
                        expectedResult.toList().map {
                            if (it is Number) it.toLong() else it
                        },
                        (result as List<*>).map {
                            if (it is Number) it.toLong() else it
                        }
                    )
                }
                else -> {
                    assertEquals(expectedResult, result)
                }
            }
        }
    }
}
