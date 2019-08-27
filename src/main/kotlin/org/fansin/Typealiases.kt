package org.fansin

import org.fansin.tcp.TcpClient

typealias TcpClientAction = (TcpClient) -> Unit
typealias TcpClientMessage = (String, TcpClient) -> Unit
