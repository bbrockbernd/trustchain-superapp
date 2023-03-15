package nl.tudelft.trustchain.detoks

import nl.tudelft.ipv8.keyvault.PublicKey

data class Token(

    // Token id
    val id: String,

    // Value of the token: 1 or 0
    val value: Int,

    // Public key of the sender
    val sender: PublicKey,

    // Public Key of the recipient
    val recipient: PublicKey,
)
