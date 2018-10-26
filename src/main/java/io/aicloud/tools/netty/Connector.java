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

    public Connector(ConnectorOptions options, Server... targets) {
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
        return isConnected && null != channel && channel.isOpen() && channel.isActive();
    }

    public void addServer(String ip, int port) {
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
                        options.getConnectHandler().onFailed(target.getHostName(), target.getPort());
                        if (options.isAllowReconnect()) {
                            connect0();

                            if (isConnected()) {
                                options.getConnectHandler().onReconnected(target.getHostName(), target.getPort());
                            } else {
                                options.getConnectHandler().onFailed(target.getHostName(), target.getPort());
                            }
                        }
                    }
                } catch (Throwable e) {
                }
            }, options.getHeathCheckInterval(), options.getHeathCheckInterval(), TimeUnit.SECONDS);
        }

        return isConnected();
    }

    @Override
    public void close() {
        if (null != options.getGroup()) {
            channel.close();
            isConnected = false;
        }
    }

    private void initConnector() {
        if (null != bootstrap) {
            channel.close();
            bootstrap = null;
        }

        EventLoopGroup group = null;
        if (null == options.getGroup()) {
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
            isConnected = channelFuture.isSuccess();

            if (isConnected) {
                channel = channelFuture.channel();
            }
        } catch (InterruptedException e) {
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

    class defaultConnectorHandler extends SimpleChannelInboundHandler<Object> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {
            options.getChannelAware().messageReceived(ctx.channel(), message);
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
