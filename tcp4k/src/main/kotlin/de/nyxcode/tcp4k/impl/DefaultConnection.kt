package de.nyxcode.tcp4k.impl

import de.nyxcode.tcp4k.Connection
import io.netty.channel.Channel
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

class DefaultConnection(val channel: Channel) : Connection {
    override fun close() {
        channel.close().get()
    }

    override fun send(msg: Serializable): Connection {
        channel.writeAndFlush(msg)
        return this;
    }

    override fun send(vararg msg: Serializable): Connection {
        msg.forEach { channel.write(it) }
        channel.flush()
        return this
    }

    override val open: Boolean
        get() = channel.run { isOpen && isActive && isWritable && isRegistered }

    private val state = ConcurrentHashMap<String, Any?>()

    override fun set(key: String, value: Any?) {
        if (value == null) state.remove(key)
        else state[key] = value
    }

    override fun get(key: String) = state[key]
}