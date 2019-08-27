package org.fansin.tcp

import org.fansin.TcpClientAction
import org.fansin.TcpClientMessage

data class TcpListener(
    val accept: TcpClientAction,
    val close: TcpClientAction,
    val get: TcpClientMessage
)