package io.aicloud.tools.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Description:
 * <pre>
 * Date: 2018-10-25
 * Time: 14:34
 * </pre>
 *
 * @author fagongzi
 */
public class SimpleTCPServer<T> {
    private String ip;
    private int port;
    private Options<T> options;

    private ServerBootstrap bootstrap = new ServerBootstrap();
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup;

    SimpleTCPServer(String ip, int port, Options<T> options) {
        this.options = options;
        this.ip = ip;
        this.port = port;
        this.workerGroup = new NioEventLoopGroup(options.getIoThreads());
    }

    public void start() {
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, options.getSocketBacklog())
                .option(ChannelOption.SO_SNDBUF, options.getSocketWriteBuffer())
                .option(ChannelOption.SO_RCVBUF, options.getSocketReadBuffer())
                .option(ChannelOption.SO_TIMEOUT, options.getSocketTimeout())
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        if (options.isDebug()) {
                            p.addLast("log", new LoggingHandler(LogLevel.DEBUG));
                        }

                        if (options.isAllowTimeout()) {
                            p.addLast(
                                    "timeout",
                                    new IdleStateHandler(options.getReadTimeout(), options
                                            .getWriteTimeout(), options.getAllTimeout()));
                        }

                        p.addLast("binary-decode", new LengthFieldBasedFrameDecoder(options.getMaxBodySize(),
                                0, 4, 0, 4));
                        p.addLast("message-decode", new NettyDecodeAdapter<>(options));
                        p.addLast("message-encode", new NettyEncodeAdapter<>(options));
                        p.addLast(new ReceivedHandler());
                    }
                });
        bootstrap.bind(ip, port);
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    class ReceivedHandler extends SimpleChannelInboundHandler<T> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, T message) throws Exception {
            messageReceived(ctx, message);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent stateEvent = (IdleStateEvent) evt;

                switch (stateEvent.state()) {
                    case WRITER_IDLE:
                        if (null != options.getTimeoutHandler()) {
                            options.getTimeoutHandler().onWriteTimeout(ctx.channel());
                        } else if (null != options.getHeartbeat()) {
                            ctx.channel().writeAndFlush(options.getHeartbeat());
                        }
                        break;
                    case READER_IDLE:
                        if (null != options.getTimeoutHandler()) {
                            options.getTimeoutHandler().onReadTimeout(ctx.channel());
                        }
                        break;
                    case ALL_IDLE:
                        if (null != options.getTimeoutHandler()) {
                            options.getTimeoutHandler().onAllTimeout(ctx.channel());
                        }
                        break;
                }
            }

            super.userEventTriggered(ctx, evt);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            if (options.getChannelAware() != null) {
                options.getChannelAware().onChannelClosed(ctx.channel());
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            if (options.getChannelAware() != null) {
                options.getChannelAware().onChannelConnected(ctx.channel());
            }
        }

        /**
         * receive a message
         *
         * @param ctx     channel ctx
         * @param message message
         */
        private void messageReceived(ChannelHandlerContext ctx, T message) {
            if (options.getChannelAware() != null) {
                options.getChannelAware().messageReceived(ctx.channel(), message);
            }
        }
    }
}
