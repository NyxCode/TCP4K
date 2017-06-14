package de.nyxcode.tcp4k

interface Event

class ConnectionEstablishedEvent(val connection: Connection): Event

class ConnectionClosedEvent(val connection: Connection): Event

class ConnectionExceptionEvent(val cause: Throwable): Event