package com.alphawallet.walletconnect.jsonrpc

import com.alphawallet.walletconnect.JSONRPC_VERSION
import com.alphawallet.walletconnect.models.WCMethod

data class JsonRpcRequest<T>(
    val id: Long,
    val jsonrpc: String = JSONRPC_VERSION,
    val method: WCMethod?,
    val params: T
)