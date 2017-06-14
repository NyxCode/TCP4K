# TCP4K
TCP4K is a tiny library in Kotlin for designing packet-based application protocols for networking applications.
It's **performant** and **extremely simple** to use.

## Example

Please take a look at the directory TCP4K/chat-example - It's a small, working chat application based on TCP4K.

```kotlin
val server_config = Server.ServerConfig(port = 1234)
val server = Server(server_config)

server.handler.register<MyPacket> { connection, myPacket ->
    // A client has send a packet of type 'MyPacket'
}

...

val client_config = Client.ClientConfig(host = "localhost", port = 1234)
val client = Client(client_config)

client.handler.register<ConnectionEstablishedEvent> { connection, event -> 
    val message = MyPacket(...)
    connection.send(message)
}
```

## Documentation
TCP4K is based on listeners. Once you have created a server/client, you can register listener for specific messages
by optaining the 'ListenerHandler' of the server/client and invoking the 'register' method. You can also register listener
for events. Currently, 'ConnectionEstablishedEvent', 'ConnectionClosedEvent' and 'ConnectionExceptionEvent' are supported.
In a listener, you can access the connection through which the message came and the actual message.

```kotlin
val connection = ...
connection.send(...)    // sends a packet to the client
connection.close(...)   // closes the connection
connection[key] = value // stores data "in" the connection.
connection[key]         // gets data previously stored under 'key'
connection.open         // is this connection open & usable?

val client = ...
client.connection       // the connection to the server (may be null)
client.connected        // is the client connected to a server?
client.config           // the configuration of this client
client.handler          // the listenerhandler of this client
client.connect()        // connects to the server specified in the configuration
client.disconnect()     // disconnects from the server
client.send(...)        // sends a message to the server (= client.connection.send(...))
client.synchronize()    // Blocks the current thread until the connection to the server is closed

val server = ...
server.config           // the configuration of this server
server.handler          // the listenerhandler of this server
server.connections      // all open connections
server.start()          // starts this server
server.stop()           // stops this server
server.broadcast()      // broadcasts a message to all clients
server.synchronize()    // Blocks the current thread until the server is stopped
...

val listener = object: Listener<MyPacket> { ......... }                 // creates a listener for a specific packet
val listener = Listener<MyPacket> { connection, myPacket -> ... }       // ^^^^^^^^

val handler = ...
handler.register(MyPacket::class, myListener)                           // registers the given listener
handler.register<MyPacket>(myListener)                                  // ^^^^
handler.register(myListener)                                            // ^^^^ (type can be inferred)
handler.register<MyPacket> { connection, myPacket -> ... }              // registers a new listener

```
