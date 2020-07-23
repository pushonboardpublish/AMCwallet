package com.alphawallet.walletconnect.exceptions

import java.lang.Exception

class InvalidJsonRpcParamsException(val requestId: Long) : Exception("Invalid JSON RPC Request")
