package com.alphawallet.walletconnect.models.session

import com.alphawallet.walletconnect.models.WCPeerMeta

data class WCApproveSessionResponse(
    val approved: Boolean = true,
    val chainId: Int,
    val accounts: List<String>,
    val peerId: String?,
    val peerMeta: WCPeerMeta?
)