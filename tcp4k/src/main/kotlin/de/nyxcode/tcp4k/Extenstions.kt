package de.nyxcode.tcp4k

import io.netty.channel.epoll.Epoll

inline fun <reified T : Any> Listener(crossinline block: (Connection, T) -> Unit) = object : Listener<T> {
    override fun received(con: Connection, message: T) = block(con, message)
}

inline fun <reified T : Any> ListenerHandler.register(listener: Listener<T>) = register(T::class, listener)

inline fun <reified T : Any> ListenerHandler.register(crossinline listener: (Connection, T) -> Unit) =
        register(Listener(listener))

val epoll = Epoll.isAvailable()

val systemClassLoader: ClassLoader = ClassLoader.getSystemClassLoader()