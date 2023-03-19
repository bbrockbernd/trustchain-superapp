package nl.tudelft.trustchain.detoks_engine

import TestMessage
import kotlinx.coroutines.delay
import mu.KotlinLogging
import nl.tudelft.ipv8.Community
import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.messaging.Packet
import kotlinx.coroutines.*

private val logger = KotlinLogging.logger {}

class TransactionCommunity: Community() {
    override val serviceId = "02315685d1932a144279f8248fc3db5899c5df8c"
    private var handler: (msg: String) -> Unit = logger::debug
    private var tokensSend: ArrayList<String>
    init {
        messageHandlers[MessageId.Test_Message] = ::onMessage
        messageHandlers[MessageId.Ack] = ::onAck
        tokensSend = ArrayList<String>()
    }

    fun setHandler(onMsg: (msg: String) -> Unit) {
        handler = onMsg
    }

    private fun onMessage(packet: Packet) {
        val (peer, payload) = packet.getAuthPayload(TestMessage.Deserializer)
        logger.debug("Message received, ${peer.mid}", peer.mid + ": " + payload.message)
        handler("DemoCommunity, ${peer.mid} : ${payload.message}")
        sendAck(peer, payload.message)
    }

    private fun onAck(packet: Packet) {
        val (peer, payload) = packet.getAuthPayload(AckMessage.Deserializer)
        val removed = tokensSend.remove(payload.message);
        logger.debug("Ack, ${peer.mid}, ${payload.message}, ${removed}", peer.mid + ": " + payload.message)
    }

    fun send(peer: Peer, token: String) {
        val packet = serializePacket(MessageId.Test_Message, TestMessage(token))
        tokensSend.add(token);
        logger.debug("Send")
        send(peer.address, packet)
        runBlocking {
            launch {
                resendMessage(peer, token)
            }
        }
    }

    private fun sendAck(peer: Peer, token: String) {
        val packet = serializePacket(MessageId.Ack, AckMessage(token))
        logger.debug("Send ack")
        send(peer.address, packet)
    }

    fun broadcastGreeting() {
        for (peer in getPeers()) {
            val packet = serializePacket(
                MessageId.Test_Message,
                TestMessage("Hello!")
            )
            send(peer.address, packet)
        }
    }

    private suspend fun resendMessage(peer: Peer , token: String) {
        delay(10000)
        if(!tokensSend.contains(token)) {
            logger.debug("Dont message")
            return
        }
        logger.debug("Resend message")
        send(peer, token)
    }

    object MessageId {
        const val Test_Message = 1
        const val Ack = 2
    }
}
