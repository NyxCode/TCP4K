package de.nyxcode.tcp4k

import de.nyxcode.tcp4k.impl.DefaultClient
import de.nyxcode.tcp4k.impl.DefaultListenerHandler
import java.io.Closeable
import java.io.Serializable

interface Client : Closeable {
    val connection: Connection?

    val connected: Boolean

    val config: ClientConfig

    val handler: ListenerHandler

    fun connect()

    fun disconnect()

    fun send(msg: Serializable)

    fun send(vararg msg: Serializable)

    fun synchronize()

    class ClientConfig(val host: String,
                       val port: Int,
                       val ioThreads: Int = 1)

    companion object {
        fun create(config: ClientConfig, handler: ListenerHandler = DefaultListenerHandler()) =
                DefaultClient(config, handler)
    }
}