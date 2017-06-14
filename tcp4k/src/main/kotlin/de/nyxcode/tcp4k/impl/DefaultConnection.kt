package de.nyxcode.tcp4k.impl

import de.nyxcode.tcp4k.Connection
import io.netty.channel.Channel
import java.util.concurrent.ConcurrentHashMap

class DefaultConnection(override val channel: Channel) : Connection {
    private val state = ConcurrentHashMap<String, Any?>()

    override fun set(key: String, value: Any?) {
        state[key] = value
    }

    override fun get(key: String) = state[key]
}