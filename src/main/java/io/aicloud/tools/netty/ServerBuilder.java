package io.aicloud.tools.netty;

/**
 * Description:
 * <pre>
 * Date: 2018-10-25
 * Time: 10:23
 * </pre>
 *
 * @author fagongzi
 */
public class ServerBuilder<T> {
    private String ip;
    private int port;
    private Options<T> options = new Options<>();

    public ServerBuilder(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public ServerBuilder<T> debug(boolean debug) {
        options.setDebug(debug);
        return this;
    }

    public ServerBuilder<T> socketSendAndRecvBuffer(int recv, int send) {
        options.setSocketReadBuffer(recv);
        options.setSocketWriteBuffer(send);
        return this;
    }

    public ServerBuilder<T> socketBacklog(int backlog) {
        options.setSocketBacklog(backlog);
        return this;
    }

    public ServerBuilder<T> socketTimeoutSeconds(int timeout) {
        options.setSocketTimeout(timeout);
        return this;
    }

    public ServerBuilder<T> maxPacketBodySize(int size) {
        options.setMaxBodySize(size);
        return this;
    }

    public ServerBuilder<T> readTimeoutSeconds(int timeout) {
        options.setAllowTimeout(true);
        options.setReadTimeout(timeout);
        return this;
    }

    public ServerBuilder<T> writeTimeoutSeconds(int timeout) {
        options.setAllowTimeout(true);
        options.setWriteTimeout(timeout);
        return this;
    }

    public ServerBuilder<T> heartbeat(Object heartbeat) {
        options.setAllowTimeout(true);
        options.setHeartbeat(heartbeat);
        return this;
    }

    public ServerBuilder<T> codec(Codec<T> codec) {
        options.setCodec(codec);
        return this;
    }

    public ServerBuilder<T> channelAware(ChannelAware<T> aware) {
        options.setChannelAware(aware);
        return this;
    }

    public ServerBuilder<T> ioProcessors(int count) {
        options.setIoThreads(count);
        return this;
    }

    public ServerBuilder<T> timeoutHander(TimeoutHandler handler) {
        options.setTimeoutHandler(handler);
        return this;
    }

    public SimpleTCPServer<T> build() {
        return new SimpleTCPServer<>(ip, port, options);
    }
}
