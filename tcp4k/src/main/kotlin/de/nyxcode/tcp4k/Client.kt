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

    fun connect(): Client

    fun disconnect(): Client

    fun send(msg: Serializable): Client

    fun send(vararg msg: Serializable): Client

    fun synchronize(): Client

    class ClientConfig(val host: String,
                       val port: Int,
                       val ioThreads: Int = 1)

    companion object {
        fun create(config: ClientConfig, handler: ListenerHandler = DefaultListenerHandler()) =
                DefaultClient(config, handler)
    }
}