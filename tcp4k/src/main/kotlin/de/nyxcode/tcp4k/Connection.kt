package de.nyxcode.tcp4k

import io.netty.channel.Channel
import java.io.Closeable
import java.io.Serializable

interface Connection: Closeable {
    val channel: Channel

    override fun close() {
        channel.close().get()
    }

    fun send(obj: Serializable): Connection {
        channel.writeAndFlush(obj)
        return this
    }

    fun send(vararg obj: Serializable): Connection {
        obj.forEach { channel.write(it) }
        channel.flush()
        return this
    }

    val open: Boolean
        get() = channel.run { isOpen && isActive && isWritable && isRegistered }

    operator fun set(key: String, value: Any?)

    operator fun get(key: String): Any?
}