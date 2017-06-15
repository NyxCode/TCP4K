import de.nyxcode.tcp4k.*
import java.time.LocalDateTime

fun main(args: Array<String>) {
    val config = Server.ServerConfig(port = 8888)
    val server = Server.create(config)
    val handler = server.handler

    handler.apply {
        register<ConnectionExceptionEvent> { connection, _ -> connection.close() }

        register<ConnectionClosedEvent> { connection, _ ->
            val connected = connection.nickname != null
            if (connected) trigger(connection, PacketDisconnect())
        }

        register<PacketConnect> { connection, (nickname) ->
            connection.nickname = nickname
            val response = PacketUserConnected(nickname, now())
            server.broadcast(response)
        }

        register<PacketDisconnect> { connection, _ ->
            val nickname = connection.nickname!!
            connection.nickname = null
            connection.close()
            server.broadcast(msg = PacketUserDisconnected(nickname, now()))
        }

        register<PacketOutgoingMessage> { connection, (message) ->
            val nickname = connection.nickname!!
            server.broadcast(msg = PacketIncomingMessage(nickname, message, now()))
        }
    }
    server.start().synchronize()
}

fun now(): LocalDateTime = LocalDateTime.now()

var Connection.nickname: String?
    get() {
        return this["nickname"] as? String?
    }
    set(value) {
        this["nickname"] = value
    }