package com.alphawallet.walletconnect.models.session

import com.alphawallet.walletconnect.models.WCPeerMeta

data class WCSessionRequest(
    val peerId: String,
    val peerMeta: WCPeerMeta,
    val chainId: String?
)