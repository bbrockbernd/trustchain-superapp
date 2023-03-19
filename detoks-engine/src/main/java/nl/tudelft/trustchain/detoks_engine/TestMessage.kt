import nl.tudelft.ipv8.messaging.Deserializable
import nl.tudelft.ipv8.messaging.serializeVarLen
import nl.tudelft.ipv8.messaging.deserializeVarLen

class TestMessage(val message: String) : nl.tudelft.ipv8.messaging.Serializable {
    override fun serialize(): ByteArray {
        return serializeVarLen(message.toByteArray())
    }

    companion object Deserializer : Deserializable<TestMessage> {
        override fun deserialize(buffer: ByteArray, offset: Int): Pair<TestMessage, Int> {
            var localOffset = offset
            val (message, messageSize) = deserializeVarLen(buffer, localOffset)
            localOffset += messageSize

            return Pair(
                TestMessage(message.toString(Charsets.UTF_8)),
                localOffset - offset
            )
        }
    }
}
