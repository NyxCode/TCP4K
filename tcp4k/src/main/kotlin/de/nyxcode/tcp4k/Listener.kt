package de.nyxcode.tcp4k

/**
 * A [Listener] which listens for a message of type [T]
 */
interface Listener<in T> {
    /**
     * Invoked when a message of type [T] is received
     */
    fun received(con: Connection, message: T)
}

