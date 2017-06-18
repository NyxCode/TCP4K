package de.nyxcode.tcp4k

import de.nyxcode.tcp4k.impl.DefaultListenerHandler
import kotlin.reflect.KClass

/**
 * Handles [Listener]
 */
interface ListenerHandler {
    /**
     * Registers a [Listener]
     */
    fun <T : Any> register(clazz: KClass<T>, listener: Listener<T>)

    /**
     * Triggers all [Listener] listening for messages of type [T]
     */
    fun <T : Any> trigger(con: Connection, obj: T)

    companion object {
        fun create() = DefaultListenerHandler()
    }
}
