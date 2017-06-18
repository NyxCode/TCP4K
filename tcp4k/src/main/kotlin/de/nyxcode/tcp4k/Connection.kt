package de.nyxcode.tcp4k

import java.io.Closeable
import java.io.Serializable

/**
 * A connection between a [Client] and a [Server]
 */
interface Connection: Closeable {
    /**
     * Closes this [Connection]
     */
    override fun close()

    /**
     * Sends a message through this [Connection]
     */
    fun send(msg: Serializable): Connection

    /**
     * Sends multiple messages through this [Connection]
     */
    fun send(vararg msg: Serializable): Connection

    /**
     * If this [Connection] is open
     */
    val open: Boolean

    /**
     * Stores state in this [Connection]
     */
    operator fun set(key: String, value: Any?)

    /**
     * Gets state stored in this [Connection]
     */
    operator fun get(key: String): Any?
}