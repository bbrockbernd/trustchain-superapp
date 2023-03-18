package nl.tudelft.trustchain.detoks_engine

import TestMessage
import mu.KotlinLogging
import nl.tudelft.ipv8.Community
import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.messaging.Packet

private const val MESSAGE_ID = 1
private val logger = KotlinLogging.logger {}

class TransactionCommunity: Community() {
    override val serviceId = "02315685d1932a144279f8248fc3db5899c5df8c"
    private var handler: (msg: String) -> Unit = logger::debug

    init {
        messageHandlers[MESSAGE_ID] = ::onMessage
        messageHandlers[MessageId.Ack] = ::onAck
    }

    fun setHandler(onMsg: (msg: String) -> Unit) {
        handler = onMsg
    }

    private fun onMessage(packet: Packet) {
        val (peer, payload) = packet.getAuthPayload(TestMessage.Deserializer)
        logger.debug("DemoCommunity, ${peer.mid}", peer.mid + ": " + payload.message)
        handler("DemoCommunity, ${peer.mid} : ${payload.message}")
        sendAck(peer)
    }

    private fun onAck(packet: Packet) {
        val (peer, payload) = packet.getAuthPayload(TestMessage.Deserializer)
        logger.debug("Ack", peer.mid + ": " + payload.message)
    }

    fun send(peer: Peer, token: String) {
        val packet = serializePacket(MESSAGE_ID, TestMessage(token))
        logger.debug("Send")
        send(peer.address, packet)
    }

    private fun sendAck(peer: Peer) {
        val packet = serializePacket(MESSAGE_ID, TestMessage("not received"))
        logger.debug("Send ack")
        send(peer.address, packet)
    }

    fun broadcastGreeting() {
        for (peer in getPeers()) {
            val packet = serializePacket(
                MESSAGE_ID,
                TestMessage("Hello!")
            )
            send(peer.address, packet)
        }
    }

    object MessageId {
        const val Test_Message = 1
        const val Ack = 1
    }
}
