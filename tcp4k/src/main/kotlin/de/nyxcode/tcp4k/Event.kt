package de.nyxcode.tcp4k

interface Event

data class ConnectionEstablishedEvent(val connection: Connection): Event

data class ConnectionClosedEvent(val connection: Connection): Event

data class ConnectionExceptionEvent(val cause: Throwable): Event