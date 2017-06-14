import de.nyxcode.tcp4k.ConnectionClosedEvent
import de.nyxcode.tcp4k.Server
import de.nyxcode.tcp4k.register
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("Server")
    val config = Server.ServerConfig(port = 8888)
    val server = Server.create(config)
    val handler = server.handler

    handler.register<Any> { _, msg -> log.info("received ${msg::class.simpleName}") }

    handler.register<ConnectionClosedEvent> { con, _ ->
        val connected = con["connected"] != null
        if(connected) handler.trigger(con, PacketDisconnect())
    }

    handler.register<PacketConnect> { connection, (nickname) ->
        connection["nickname"] = nickname
        val response = PacketUserConnected(nickname, now())
        server.broadcast(response)
    }

    handler.register<PacketDisconnect> { connection, _ ->
        val nickname = connection["nickname"] as String
        connection["nickname"] = null
        val response = PacketUserDisconnected(nickname, now())
        connection.close()
        server.broadcast(response)
    }

    handler.register<PacketOutgoingMessage> { con, (message) ->
        val nickname = con["nickname"] as String
        val response = PacketIncomingMessage(nickname, message, now())
        server.broadcast(response)
    }

    server.start()
    server.synchronize()
}

fun now(): LocalDateTime = LocalDateTime.now()