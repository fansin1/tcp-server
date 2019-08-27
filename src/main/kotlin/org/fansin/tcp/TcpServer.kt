package org.fansin.tcp

import kotlinx.coroutines.*
import java.net.ServerSocket

class TcpServer(port: Int, timeout: Int = 5000) : CoroutineScope {

    override val coroutineContext = Dispatchers.Default

    private val serverSocket = ServerSocket(port)
    private val clients = mutableListOf<TcpClient>()
    private val listeners = mutableListOf<TcpListener>()

    private var working = true
    private val accepterJob: Job

    init {
        accepterJob = launch(Dispatchers.IO) {
            while (working) {
                val newSocket = serverSocket.accept() // Wait for accept
                newSocket.soTimeout = timeout

                val newClient = TcpClient(newSocket)

                listeners.forEach { listener ->
                    listener.accept(newClient)
                }

                newClient.getListener = { msg ->
                    listeners.forEach { listener ->
                        listener.get(msg, newClient)
                    }
                }

                newClient.closeListener = {
                    listeners.forEach { listener ->
                        clients.remove(newClient)
                        listener.close(newClient)
                    }
                }

                clients.add(newClient)
            }
        }
    }

    fun addListener(listener: TcpListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: TcpListener) {
        listeners.remove(listener)
    }

    fun stop() {
        working = false
    }

    suspend fun join() {
        accepterJob.join()
    }

}