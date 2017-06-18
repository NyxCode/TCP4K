package de.nyxcode.tcp4k

interface Event

class ConnectionEstablishedEvent: Event

class ConnectionClosedEvent: Event

data class ConnectionExceptionEvent(val cause: Throwable): Event