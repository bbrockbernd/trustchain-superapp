import os
import sys
import random
from asyncio import ensure_future, get_event_loop
from torrentool.api import Torrent

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

from pyipv8.ipv8_service import IPv8

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

        async def print_peers():
            print(
                "I am:", self.my_peer, "\nI know:", [str(p) for p in self.get_peers()]
            )

        self.register_task("print_peers", print_peers, interval=5.0, delay=0)

        self.add_message_handler(1, self.on_message)

    def on_message(self, peer, payload):
        print("Got a message from:", peer)
        print("The message includes the first payload:\n", payload)


async def start_nodes(num_nodes):
    if not os.path.exists("keys/"):
        os.mkdir("keys/")

    if not os.path.exists("torrents/"):
        os.mkdir("torrents/")

    for i in range(num_nodes):
        node_name = f"dummy_peer_{i}"
        builder = ConfigBuilder()
        builder.add_key(node_name, "medium", f"keys/ec{i}.pem")
        builder.add_overlay(
            "TransactionCommunity",
            node_name,
            [WalkerDefinition(Strategy.RandomWalk, 10, {"timeout": 20})],
            default_bootstrap_defs,
            {},
            [("started",)],
        )
        ipv8 = IPv8(
            builder.finalize(), extra_communities={"TransactionCommunity": TransactionCommunity}
        )
        await ipv8.start()


if __name__ == "__main__":
    try:
        num_nodes = int(sys.argv[1])
    except:
        print("Please provide a number of nodes to spawn")
        exit(1)

    ensure_future(start_nodes(num_nodes))
    get_event_loop().run_forever()