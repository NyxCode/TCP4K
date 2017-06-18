package de.nyxcode.tcp4k

import de.nyxcode.tcp4k.impl.DefaultListenerHandler
import de.nyxcode.tcp4k.impl.DefaultServer
import java.io.Closeable
import java.io.Serializable

/**
 * A TCP server
 */
interface Server: Closeable {
    /**
     * The configuration of this [Server]
     */
    val config: ServerConfig

    /**
     * The [ListenerHandler] of this [Server] which is used to handle all events and incoming packets
     */
    val handler: ListenerHandler

    /**
     * The connections of all connected [Client]s
     */
    val connections: Collection<Connection>

    /**
     * Starts this [Server]
     */
    fun start(): Server

    /**
     * Stops this [Server]
     */
    fun stop(): Server

    /**
     * Blocks the current thread until this [Server] is stopped
     */
    fun synchronize(): Server

    /**
     * Sends a message to all connected [Client]s
     */
    fun broadcast(msg: Serializable): Server

    class ServerConfig(val port: Int,
                       val bossIoThreads: Int = 1,
                       val workerIoThreads: Int = 1)

    companion object {
        fun create(config: ServerConfig, handler: ListenerHandler = DefaultListenerHandler()) =
                DefaultServer(config, handler)
    }
}