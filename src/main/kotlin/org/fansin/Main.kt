package org.fansin

import com.sun.deploy.net.HttpRequest
import kotlinx.coroutines.runBlocking
import org.fansin.tcp.TcpClient
import org.fansin.tcp.TcpListener
import org.fansin.tcp.TcpServer

object Main {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val server = TcpServer(9191)
        server.addListener(
            TcpListener(
                Main::accept,
                Main::close,
                Main::get
            )
        )
        server.join()
    }

    private fun accept(client: TcpClient) {
        println("$client connected")
    }

    private fun close(client: TcpClient) {
        println("$client disconnected")
    }

    private fun get(msg: String, client: TcpClient) {
        println("$client:      $msg")
        client.send(
            "HTTP/1.1 200 OK\r\n" +
            "Server: Server\r\n" +
            "Content-Type: text/html\r\n" +
            "Content-Length: 10\r\n" +
            "Connection: close\r\n\r\n" +
            "12345678901")
    }

}