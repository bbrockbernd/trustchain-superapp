package nl.tudelft.trustchain.detoks_engine

import nl.tudelft.ipv8.messaging.Deserializable
import nl.tudelft.ipv8.messaging.deserializeVarLen
import nl.tudelft.ipv8.messaging.serializeVarLen

class AckMessage(val message: String) : nl.tudelft.ipv8.messaging.Serializable {

    override fun serialize(): ByteArray {
        return serializeVarLen(message.toByteArray())
    }

    companion object Deserializer : Deserializable<AckMessage> {
        override fun deserialize(buffer: ByteArray, offset: Int): Pair<AckMessage, Int> {
            var localOffset = offset
            val (message, messageSize) = deserializeVarLen(buffer, localOffset)
            localOffset += messageSize

            return Pair(
                AckMessage(message.toString(Charsets.UTF_8)),
                localOffset - offset
            )
        }
    }
}
