package de.nyxcode.tcp4k

interface Listener<in T> {
    fun received(con: Connection, message: T)
}

