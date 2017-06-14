
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

    client.handler.register<PacketUserConnected> { _, (nickname, timestamp) ->
        println("${timestamp.formatted} | $nickname connected!")
    }

    client.handler.register<PacketUserDisconnected> { _, (nickname, timestamp) ->
        println("${timestamp.formatted} | $nickname disconnected!")
    }

    client.handler.register<PacketIncomingMessage> { _, (nickname, message, timestamp) ->
        println("${timestamp.formatted} | $nickname: $message")
    }

    client.connect()
    client.send(PacketConnect(nickname))

    while(true) {
        val message = input.nextLine()
        val packet = PacketOutgoingMessage(message)
        client.send(packet)
    }
}

val LocalDateTime.formatted: String
    get() = this.format(formatter)

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
