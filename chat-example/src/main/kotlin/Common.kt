
import java.io.Serializable
import java.time.LocalDateTime

/**
 * Send by a client to the server to open the connection
 */
data class PacketConnect(val nickname: String) : Serializable

/**
 * Send by the server to all clients when a client connects to the server
 */
data class PacketUserConnected(val nickname: String,
                               val timestamp: LocalDateTime) : Serializable


/**
 * Send by a client to the server to close the connection
 */
class PacketDisconnect : Serializable

/**
 * Send by the server to all clients when a client sends a message
 */
data class PacketUserDisconnected(val nickname: String,
                                  val timestamp: LocalDateTime) : Serializable


/**
 * Send by a client to the server when the client sends a message
 */
data class PacketOutgoingMessage(val message: String) : Serializable

/**
 * Send by the server to all clients when a client sends a mesage
 */
data class PacketIncomingMessage(val nickname: String,
                                 val message: String,
                                 val timestamp: LocalDateTime) : Serializable