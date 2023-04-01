import os
import sys
import random
from asyncio import ensure_future, get_event_loop
from dataclasses import dataclass
from torrentool.api import Torrent

from pyipv8.ipv8.lazy_community import lazy_wrapper
from pyipv8.ipv8.community import Community
from pyipv8.ipv8.messaging.serialization import Serializable
from pyipv8.ipv8.configuration import (
    ConfigBuilder,
    Strategy,
    WalkerDefinition,
    default_bootstrap_defs,
    BootstrapperDefinition,
    Bootstrapper,
)
from pyipv8.ipv8.messaging.payload_dataclass import overwrite_dataclass
from pyipv8.ipv8_service import IPv8

dataclass = overwrite_dataclass(dataclass)

MESSAGE_TORRENT_ID = 1

bootstrap_config = {
    "class": "DispersyBootstrapper",
    "init": {
        "ip_addresses": [
            ("130.161.119.206", 6421),
            ("130.161.119.206", 6422),
            ("131.180.27.155", 6423),
            ("131.180.27.156", 6424),
            ("131.180.27.161", 6427),
            ("131.180.27.161", 6521),
            ("131.180.27.161", 6522),
            ("131.180.27.162", 6523),
            ("131.180.27.162", 6524),
            ("130.161.119.215", 6525),
            ("130.161.119.215", 6526),
            ("130.161.119.201", 6527),
            ("130.161.119.201", 6528),
            ("131.180.27.188", 1337),
            ("131.180.27.187", 1337),
            ("131.180.27.161", 6427),
            ("86.92.219.31", 6621),
            ("86.92.219.31", 6622),
            ('192.168.1.100', 12345),
            ("86.92.219.31", 8001)
        ],
        "dns_addresses": [
            ("dispersy1.tribler.org", 6421),
            ("dispersy1.st.tudelft.nl", 6421),
            ("dispersy2.tribler.org", 6422),
            ("dispersy2.st.tudelft.nl", 6422),
            ("dispersy3.tribler.org", 6423),
            ("dispersy3.st.tudelft.nl", 6423),
            ("dispersy4.tribler.org", 6424),
            ("tracker1.ip-v8.org", 6521),
            ("tracker2.ip-v8.org", 6522),
            ("tracker3.ip-v8.org", 6523),
            ("tracker4.ip-v8.org", 6524),
            ("tracker5.ip-v8.org", 6525),
            ("tracker6.ip-v8.org", 6526),
            ("tracker7.ip-v8.org", 6527),
            ("tracker8.ip-v8.org", 6528),
        ],
        "bootstrap_timeout": 0.5,
    },
}


bootstrapper = [
    BootstrapperDefinition(Bootstrapper.DispersyBootstrapper, bootstrap_config["init"])
]

@dataclass(msg_id=1)  # The (byte) value 1 identifies this message and must be unique per community
class MyMessage:
    clock: int  # We add an integer (technically a "long long") field "clock" to this message

class MyCommunityMessage(Serializable):
    def __init__(self, message):
        self.message = message

    def to_pack_list(self):
        return [("Q", 1), ("raw", self.message)]

    @classmethod
    def from_unpack_list(cls, *args):
        return cls(*args)

class TorrentPayload(Serializable):
    def __init__(self, message):
        self.message = message

    def to_pack_list(self):
        return [("20s", self.message.encode())]

    @classmethod
    def from_unpack_list(cls, *args):
        return cls(*args)


class TransactionCommunity(Community):
    community_id = bytes.fromhex("02315685d1932a144279f8248fc3db5899c5df8c")

    def started(self):
        self.torrent_list = os.listdir("torrents/")

        # async def print_peers():
        #     print(
        #         "I am:", self.my_peer, "\nI know:", [str(p) for p in self.get_peers()]
        #     )
        #     self.send_message_to_peers(MyCommunityMessage(b"Hello, world!"))

        async def send_message_to_peers():
            print(
                "I am:", self.my_peer, "\nI know:", [str(p) for p in self.get_peers()]
            )
            for peer in self.get_peers():
                self.ez_send(peer, MyMessage(1))


        # self.register_task("print_peers", print_peers, interval=5.0, delay=0)

        self.register_task("send_message_to_peers", send_message_to_peers, interval=5.0, delay=0)

        self.add_message_handler(1, self.on_message)

    def on_message(self, peer, payload):
        print("Got a message from:", peer)
        print("The message includes the first payload:\n", payload)


async def start_nodes(num_nodes: int, timeout=20, max_peers=3):
    if not os.path.exists("keys/"):
        os.mkdir("keys/")

    if not os.path.exists("torrents/"):
        os.mkdir("torrents/")

    for i in range(num_nodes):
        node_name = f"dummy_peer_{i}"
        builder = ConfigBuilder()
        builder.add_key(node_name, "medium", f"ec{i}.pem")
        builder.add_overlay(
            "TransactionCommunity",
            node_name,
            [WalkerDefinition(Strategy.RandomWalk, max_peers, {"timeout": timeout})],
            default_bootstrap_defs,
            {},
            [("started",)],
        )
        ipv8 = IPv8(
            builder.finalize(), extra_communities={"TransactionCommunity": TransactionCommunity}
        )
        await ipv8.start()


if __name__ == "__main__":
    NUM_NODES = int(os.getenv("NUM_NODES"))
    TIMEOUT = int(os.getenv("TIMEOUT"))
    MAX_NUM_PEERS = int(os.getenv("MAX_NUM_PEERS"))

    ensure_future(start_nodes(NUM_NODES, TIMEOUT, MAX_NUM_PEERS))
    get_event_loop().run_forever()