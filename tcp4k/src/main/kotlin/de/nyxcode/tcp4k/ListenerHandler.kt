package de.nyxcode.tcp4k

import de.nyxcode.tcp4k.impl.DefaultListenerHandler
import kotlin.reflect.KClass

interface ListenerHandler {
    fun <T : Any> register(clazz: KClass<T>, listener: Listener<T>)

    fun <T : Any> trigger(con: Connection, obj: T)

    companion object {
        fun create() = DefaultListenerHandler()
    }
}
