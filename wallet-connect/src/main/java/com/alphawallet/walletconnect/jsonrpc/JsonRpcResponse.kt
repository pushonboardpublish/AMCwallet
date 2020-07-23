package com.alphawallet.walletconnect.jsonrpc

import com.alphawallet.walletconnect.JSONRPC_VERSION

data class JsonRpcResponse<T> (
    val jsonrpc: String = JSONRPC_VERSION,
    val id: Long,
    val result: T
)

data class JsonRpcErrorResponse (
    val jsonrpc: String = JSONRPC_VERSION,
    val id: Long,
    val error: JsonRpcError
)