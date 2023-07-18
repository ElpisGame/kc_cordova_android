package com.riceball.gpknives.connect

interface BitverseConnectDelegate {
    fun failedToConnect() // 断开websocket链接
    fun didConnect(chainId: Int?,accounts: List<String>?) // 成功选择连接钱包
    fun didDisconnect() // 断开和钱包的连接，针对于disconnect
}