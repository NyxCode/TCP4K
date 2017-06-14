package de.nyxcode.tcp4k.impl

import de.nyxcode.tcp4k.*
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.serialization.ClassResolvers
import io.netty.handler.codec.serialization.ObjectDecoder
import io.netty.handler.codec.serialization.ObjectEncoder
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import mu.KLogging
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

class DefaultServer(override val config: Server.ServerConfig,
                    override val handler: ListenerHandler = DefaultListenerHandler()) : Server {

    companion object : KLogging()

    private val bossEventLoop = createEventLoop(config.bossIoThreads)
    private val workerEventLoop = createEventLoop(config.workerIoThreads)
    private val _connections = ConcurrentHashMap<ChannelId, Connection>()
    private var closed = false
    override var channel: Channel? = null

    override val connections: Collection<Connection>
        get() = _connections.values

    override fun start() {
        checkState()
        checkStopped()
        val future = createBootstrap().bind()
        future.get()
        channel = future.channel()
        logger.info("Server successfully started!")
    }

    override fun stop() {
        checkState()
        checkStarted()
        channel?.close()?.get()
        logger.info("Server successfully stopped!")
    }

    override fun close() {
        checkState()
        if(channel != null) stop()
        closed = true
        bossEventLoop.shutdownGracefully().get()
        workerEventLoop.shutdownGracefully().get()
    }

    override fun synchronize() {
        checkState()
        checkStarted()
        channel?.closeFuture()?.sync()
    }

    override fun broadcast(msg: Serializable) = connections.forEach { it.send(msg) }

    private fun checkState() {
        if(closed) throw IllegalStateException("Closed!")
    }

    private fun checkStarted() {
        if(channel == null) throw IllegalStateException("Not started!")
    }

    private fun checkStopped() {
        if(channel != null) throw IllegalStateException("Running!")
    }

    private fun createBootstrap() = ServerBootstrap()
            .localAddress(config.port)
            .group(bossEventLoop, workerEventLoop)
            .handler(LoggingHandler(LogLevel.DEBUG))
            .childHandler(ServerInitializer())
            .channel(if(epoll) EpollServerSocketChannel::class.java else NioServerSocketChannel::class.java)

    private fun createEventLoop(threads: Int) = if(epoll) EpollEventLoopGroup(threads) else NioEventLoopGroup(threads)

    private inner class ServerInitializer: ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
            ch.pipeline().addLast(
                    ObjectEncoder(),
                    ObjectDecoder(ClassResolvers.softCachingConcurrentResolver(systemClassLoader)),
                    ServerHandler())
        }
    }

    private inner class ServerHandler: SimpleChannelInboundHandler<Any>() {
        override fun messageReceived(ctx: ChannelHandlerContext, msg: Any) {
            val connection = _connections[ctx.channel().id()]!!
            handler.trigger(connection, msg)
        }

        override fun channelActive(ctx: ChannelHandlerContext) {
            logger.info("Client established a connection!")
            val channel = ctx.channel()
            val con = DefaultConnection(channel)
            _connections.put(channel.id(), con)
            handler.trigger(con, ConnectionEstablishedEvent(con))
        }

        override fun channelInactive(ctx: ChannelHandlerContext) {
            logger.info("Client lost connection!")
            val id = ctx.channel().id()
            val con = _connections.remove(id)!!
            handler.trigger(con, ConnectionClosedEvent(con))
        }

        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            logger.info("Exception caught! ({})", cause::class.simpleName)
            val con = _connections[ctx.channel().id()]!!
            handler.trigger(con, ConnectionExceptionEvent(cause))
        }
    }
}