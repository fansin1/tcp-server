package org.fansin.tcp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import java.net.Socket
import java.net.SocketTimeoutException

class TcpClient(
    private val socket: Socket
) : CoroutineScope {

    override val coroutineContext = Dispatchers.Default

    private val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

    private var isOpen = true

    var getListener: (String) -> Unit = {}
    var closeListener: () -> Unit = {}

    init {
        launch(Dispatchers.IO) {
            try {
                while (isOpen) {
                    val line = reader.readLine()

                    if (line == null) {
                        close()
                        break
                    }

                    getListener(line)
                    delay(100)
                }
            } catch (e: IOException) {
                close()
            } catch (e: SocketTimeoutException) {
                close()
            }
            finally {
                if (isOpen)
                    close()
            }
        }
    }

    fun send(s: String) {
        try {
            writer.write(s)
            writer.flush()
        } catch (e: IOException) {
            close()
        }
    }

    private fun close(closeable: Closeable) {
        try {
            closeable.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun close() {
        isOpen = false
        close(reader)
        close(writer)
        close(socket)
        closeListener()
    }
}