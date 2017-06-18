package de.nyxcode.tcp4k.impl

import de.nyxcode.tcp4k.Connection
import de.nyxcode.tcp4k.Listener
import de.nyxcode.tcp4k.ListenerHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

class DefaultListenerHandler: ListenerHandler {
    private val listener = ConcurrentHashMap<KClass<*>, MutableCollection<Listener<*>>>()
    private val superClasses = ConcurrentHashMap<KClass<*>, Collection<KClass<*>>>()

    override fun <T : Any> register(clazz: KClass<T>, listener: Listener<T>) {
        this.listener.computeIfAbsent(clazz) { CopyOnWriteArrayList<Listener<*>>() }.add(listener)
    }

    override fun <T : Any> trigger(con: Connection, obj: T) {
        val type = obj::class
        listener[Any::class]?.triggerAll(con, obj)
        listener[type]?.triggerAll(con, obj)
        cachedSuperClasses(type).forEach { listener[it]?.triggerAll(con, obj) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> Collection<Listener<*>>.triggerAll(con: Connection, msg: T) =
            forEach { (it as Listener<T>).received(con, msg) }

    private fun <T : Any> cachedSuperClasses(clazz: KClass<T>): Collection<KClass<*>> {
        var superclasses = superClasses[clazz]
        if(superclasses == null) {
            superclasses = clazz.superclasses
            superClasses[clazz] = superclasses
        }
        return superclasses
    }
}