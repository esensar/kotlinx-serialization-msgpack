package com.ensarsarajcic.kotlinx.serialization.msgpack.unsigned

import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.InlineDecoderHelper
import com.ensarsarajcic.kotlinx.serialization.msgpack.internal.InlineEncoderHelper
import kotlinx.serialization.builtins.serializer

@OptIn(ExperimentalUnsignedTypes::class)
object MsgPackUnsignedSupport {
    val ALL_ENCODERS =
        mapOf(
            UByte.serializer().descriptor to { helper: InlineEncoderHelper -> UByteEncoder(helper) },
            UShort.serializer().descriptor to { helper: InlineEncoderHelper -> UShortEncoder(helper) },
            UInt.serializer().descriptor to { helper: InlineEncoderHelper -> UIntEncoder(helper) },
            ULong.serializer().descriptor to { helper: InlineEncoderHelper -> ULongEncoder(helper) },
        )
    val ALL_DECODERS =
        mapOf(
            UByte.serializer().descriptor to { helper: InlineDecoderHelper -> UByteDecoder(helper) },
            UShort.serializer().descriptor to { helper: InlineDecoderHelper -> UShortDecoder(helper) },
            UInt.serializer().descriptor to { helper: InlineDecoderHelper -> UIntDecoder(helper) },
            ULong.serializer().descriptor to { helper: InlineDecoderHelper -> ULongDecoder(helper) },
        )
}
