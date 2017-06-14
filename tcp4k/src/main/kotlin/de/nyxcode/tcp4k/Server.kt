package de.nyxcode.tcp4k

import de.nyxcode.tcp4k.impl.DefaultListenerHandler
import de.nyxcode.tcp4k.impl.DefaultServer
import io.netty.channel.Channel
import java.io.Closeable
import java.io.Serializable

interface Server: Closeable {
    val config: ServerConfig

    val handler: ListenerHandler

    val connections: Collection<Connection>

    val channel: Channel?

    fun start(): Server

    fun stop(): Server

    fun synchronize(): Server

    fun broadcast(msg: Serializable): Server

    class ServerConfig(val port: Int,
                       val bossIoThreads: Int = 1,
                       val workerIoThreads: Int = 1)

    companion object {
        fun create(config: ServerConfig, handler: ListenerHandler = DefaultListenerHandler()) =
                DefaultServer(config, handler)
    }
}