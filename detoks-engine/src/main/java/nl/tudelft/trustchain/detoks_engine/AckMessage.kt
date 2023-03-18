package nl.tudelft.trustchain.detoks_engine

import nl.tudelft.ipv8.messaging.Deserializable

class AckMessage(val message: String) : nl.tudelft.ipv8.messaging.Serializable {

    override fun serialize(): ByteArray {
        return message.toByteArray()
    }

    companion object Deserializer : Deserializable<AckMessage> {
        override fun deserialize(buffer: ByteArray, offset: Int): Pair<AckMessage, Int> {
            return Pair(AckMessage(buffer.toString(Charsets.UTF_8)), buffer.size)
        }
    }
}
