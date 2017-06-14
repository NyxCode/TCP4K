package de.nyxcode.tcp4k.impl

import de.nyxcode.tcp4k.*
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.serialization.ClassResolvers
import io.netty.handler.codec.serialization.ObjectDecoder
import io.netty.handler.codec.serialization.ObjectEncoder
import mu.KLogging
import java.io.Serializable

class DefaultClient(override val config: Client.ClientConfig,
                    override val handler: ListenerHandler = DefaultListenerHandler()) : Client {
    override val connected: Boolean
        get() {
            checkState()
            return connection?.open ?: false
        }

    override fun synchronize(): DefaultClient {
        checkState()
        connection?.channel?.closeFuture()?.sync()
        return this
    }

    companion object : KLogging()

    override var connection: Connection? = null
    private val eventLoop = if (epoll) EpollEventLoopGroup(config.ioThreads) else NioEventLoopGroup(config.ioThreads)
    private var closed = false

    private fun finalize() {
        if(!eventLoop.isShutdown)
            eventLoop.shutdownGracefully().get()
    }

    override fun close() {
        checkState()
        this.closed = true
        if (connected) disconnect()
        eventLoop.shutdownGracefully().get()
    }

    override fun connect(): DefaultClient {
        checkState()
        checkDisconnected()
        val future = createBootstrap().connect(config.host, config.port)
        future.get()
        connection = DefaultConnection(future.channel())
        return this
    }

    override fun disconnect(): DefaultClient {
        checkState()
        checkConnected()
        connection?.channel?.close()?.get()
        connection = null
        return this
    }

    override fun send(msg: Serializable): DefaultClient {
        checkState()
        checkConnected()
        connection!!.send(msg)
        return this
    }

    override fun send(vararg msg: Serializable): DefaultClient {
        checkState()
        checkConnected()
        connection!!.send(*msg)
        return this
    }

    private fun createBootstrap() = Bootstrap()
            .handler(ClientInitializer())
            .group(eventLoop)
            .channel(if(epoll) EpollSocketChannel::class.java else NioSocketChannel::class.java)

    private fun checkConnected() {
        if (!connected) throw IllegalStateException("Disconnected")
    }

    private fun checkDisconnected() {
        if (connected) throw IllegalStateException("Connected")
    }

    private fun checkState() {
        if (closed) throw IllegalStateException("Closed")
    }

    private inner class ClientHandler : SimpleChannelInboundHandler<Any>() {
        override fun messageReceived(ctx: ChannelHandlerContext, msg: Any) {
            val con = connection!!
            handler.trigger(con, msg)
        }

        override fun channelActive(ctx: ChannelHandlerContext) {
            logger.info("Connection to server established!")
            val con = DefaultConnection(ctx.channel())
            connection = con
            handler.trigger(con, ConnectionEstablishedEvent(con))
        }

        override fun channelInactive(ctx: ChannelHandlerContext) {
            logger.info("Connection to server lost!")
            val con = connection!!
            handler.trigger(con, ConnectionClosedEvent(con))
            connection = null
        }

        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            logger.error("Exception caught! ({})", cause::class.simpleName)
            val con = connection!!
            handler.trigger(con, ConnectionExceptionEvent(cause))
        }
    }

    private inner class ClientInitializer : ChannelInitializer<SocketChannel>() {

        override fun initChannel(ch: SocketChannel) {
            ch.pipeline().addLast(
                    ObjectEncoder(),
                    ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(systemClassLoader)),
                    ClientHandler())
        }
    }
}