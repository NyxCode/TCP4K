import de.nyxcode.tcp4k.Client
import de.nyxcode.tcp4k.register
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    print("username: ")
    val nickname = input.nextLine()
    val config = Client.ClientConfig(host = "localhost", port = 8888)
    val client = Client.create(config = config)

    client.handler.apply {
        register<PacketUserConnected> { _, (nickname, timestamp) ->
            println("${timestamp.formatted} | $nickname connected!")
        }

        register<PacketUserDisconnected> { _, (nickname, timestamp) ->
            println("${timestamp.formatted} | $nickname disconnected!")
        }

        register<PacketIncomingMessage> { _, (nickname, message, timestamp) ->
            println("${timestamp.formatted} | $nickname: $message")
        }
    }

    client.connect()
    client.send(PacketConnect(nickname))

    while (client.connected) {
        val message = input.nextLine()
        val packet = PacketOutgoingMessage(message)
        client.send(packet)
    }
    client.close()
    input.close()
}

val LocalDateTime.formatted: String
    get() = this.format(formatter)

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
