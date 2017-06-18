package de.nyxcode.tcp4k

import de.nyxcode.tcp4k.impl.DefaultClient
import de.nyxcode.tcp4k.impl.DefaultListenerHandler
import java.io.Closeable
import java.io.Serializable

/**
 * A TCP client
 */
interface Client : Closeable {
    /**
     * The [Connection] to the [Server].
     */
    val connection: Connection?

    /**
     * If this [Client] is connected to a [Server]
     */
    val connected: Boolean

    /**
     * The [ClientConfig] of this [Client]
     */
    val config: ClientConfig

    /**
     * The [ListenerHandler] of this [Client] which is used to handle all events and incoming packets
     */
    val handler: ListenerHandler

    /**
     * Connects to the [Server] specified in the [ClientConfig]
     */
    fun connect(): Client

    /**
     * Disconnects from the [Server]
     */
    fun disconnect(): Client

    /**
     * Sends a message to the [Server]
     */
    fun send(msg: Serializable): Client

    /**
     * Sends multiple messages to the [Server]
     */
    fun send(vararg msg: Serializable): Client

    /**
     * Blocks the current thread until the [Connection] to the [Server] is closed
     */
    fun synchronize(): Client

    class ClientConfig(val host: String,
                       val port: Int,
                       val ioThreads: Int = 1)

    companion object {
        fun create(config: ClientConfig, handler: ListenerHandler = DefaultListenerHandler()) =
                DefaultClient(config, handler)
    }
}