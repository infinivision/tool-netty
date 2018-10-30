package io.aicloud.tools.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Description:
 * <pre>
 * Date: 2018-10-25
 * Time: 14:19
 * </pre>
 *
 * @author fagongzi
 */
public class Connector<T> implements IOSession<T> {
    private ConnectorOptions options;

    private Bootstrap bootstrap;
    private Channel channel;
    private boolean isConnected;
    private ChannelHandler channelHandler;

    private ReentrantReadWriteLock lock;

    private final List<InetSocketAddress> availableTargets = new ArrayList<>();
    private int opts = 0;
    private InetSocketAddress target;

    Connector(ConnectorOptions options, Server... targets) {
        this.options = options;
        this.lock = new ReentrantReadWriteLock();

        for (Server server : targets) {
            addServer(server.getIp(), server.getPort());
        }

        channelHandler = new defaultConnectorHandler();
    }

    @Override
    public boolean write(T value) {
        if (isConnected()) {
            channel.write(value);
            return true;
        }

        return false;
    }

    @Override
    public boolean writeAndFlush(T value) {
        if (isConnected()) {
            channel.writeAndFlush(value);
            return true;
        }

        return false;
    }

    @Override
    public boolean flush() {
        if (isConnected()) {
            channel.flush();
            return true;
        }

        return false;
    }

    @Override
    public boolean isConnected() {
        lock.readLock().lock();
        try {
            return isConnected && null != channel && channel.isOpen() && channel.isActive();
        } finally {
            lock.readLock().unlock();
        }
    }

    private void addServer(String ip, int port) {
        try {
            lock.writeLock().lock();
            this.availableTargets.add(new InetSocketAddress(ip, port));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean connect() {
        connect0();

        if (isConnected()) {
            options.getExecutor().scheduleAtFixedRate(() -> {
                try {
                    if (!isConnected()) {
                        options.getConnectHandler().onFailed(this, target.getHostName(), target.getPort());
                        if (options.isAllowReconnect()) {
                            connect0();

                            if (isConnected()) {
                                options.getConnectHandler().onReconnected(this, target.getHostName(), target.getPort());
                            } else {
                                options.getConnectHandler().onFailed(this, target.getHostName(), target.getPort());
                            }
                        }
                    }
                } catch (Throwable e) {
                    // ignore
                }
            }, options.getHeathCheckInterval(), options.getHeathCheckInterval(), TimeUnit.SECONDS);
        }

        return isConnected();
    }

    @Override
    public void close() {
        lock.writeLock().lock();
        try {
            if (null != bootstrap) {
                channel.close();
                isConnected = false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void initConnector() {
        lock.writeLock().lock();
        if (null != bootstrap) {
            channel.close();
            bootstrap = null;
        }
        lock.writeLock().unlock();

        EventLoopGroup group = options.getGroup();
        if (null == group) {
            group = new NioEventLoopGroup(1);
        }

        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_SNDBUF, options.getSocketWriteBuffer())
                .option(ChannelOption.SO_RCVBUF, options.getSocketReadBuffer())
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        if (options.isDebug()) {
                            p.addLast("log", new LoggingHandler(LogLevel.DEBUG));
                        }

                        if (options.getHeartbeat() != null) {
                            p.addLast(
                                    "timeout",
                                    new IdleStateHandler(options.getReadTimeout(),
                                            options.getWriteTimeout(),
                                            options.getAllTimeout(),
                                            TimeUnit.MILLISECONDS));

                        }

                        p.addLast("binary-decode", new LengthFieldBasedFrameDecoder(options.getMaxBodySize(), 0, 4,
                                0, 4));
                        p.addLast("message-decode", new NettyDecodeAdapter(options));
                        p.addLast("message-encode", new NettyEncodeAdapter(options));
                        p.addLast(channelHandler);
                    }
                });
    }

    private void connect0() {
        target = next();

        initConnector();

        try {
            ChannelFuture channelFuture = bootstrap.connect(target);
            channelFuture.await(options.getSocketTimeout());
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private InetSocketAddress next() {
        try {
            lock.readLock().lock();
            return availableTargets.get(opts++ % availableTargets.size());
        } finally {
            lock.readLock().unlock();
        }
    }

    @ChannelHandler.Sharable
    class defaultConnectorHandler extends SimpleChannelInboundHandler<Object> {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            lock.writeLock().lock();
            isConnected = true;
            channel = ctx.channel();
            lock.writeLock().unlock();

            options.getChannelAware().onChannelConnected(ctx.channel());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            options.getChannelAware().onChannelClosed(ctx.channel());
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {
            options.getChannelAware().messageReceived(ctx.channel(), message);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            options.getChannelAware().onChannelException(ctx.channel(), cause);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent stateEvent = (IdleStateEvent) evt;

                switch (stateEvent.state()) {
                    case WRITER_IDLE:
                        if (options.getHeartbeat() != null) {
                            ctx.channel().writeAndFlush(options.getHeartbeat());
                        }
                        break;
                    case READER_IDLE:
                        ctx.channel().close();
                        break;
                }
            }

            super.userEventTriggered(ctx, evt);
        }
    }
}
