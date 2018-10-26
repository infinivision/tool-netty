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
public class ServerBuilder {
    private String ip;
    private int port;
    private Options options = new Options();

    public ServerBuilder(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public ServerBuilder debug(boolean debug) {
        options.setDebug(debug);
        return this;
    }

    public ServerBuilder socketSendAndRecvBuffer(int recv, int send) {
        options.setSocketReadBuffer(recv);
        options.setSocketWriteBuffer(send);
        return this;
    }

    public ServerBuilder socketBacklog(int backlog) {
        options.setSocketBacklog(backlog);
        return this;
    }

    public ServerBuilder socketTimeoutSeconds(int timeout) {
        options.setSocketTimeout(timeout);
        return this;
    }

    public ServerBuilder maxPacketBodySize(int size) {
        options.setMaxBodySize(size);
        return this;
    }

    public ServerBuilder readTimeoutSeconds(int timeout) {
        options.setAllowTimeout(true);
        options.setReadTimeout(timeout);
        return this;
    }

    public ServerBuilder writeTimeoutSeconds(int timeout) {
        options.setAllowTimeout(true);
        options.setWriteTimeout(timeout);
        return this;
    }

    public ServerBuilder heartbeat(Object heartbeat) {
        options.setAllowTimeout(true);
        options.setHeartbeat(heartbeat);
        return this;
    }

    public ServerBuilder codec(Codec codec) {
        options.setCodec(codec);
        return this;
    }

    public ServerBuilder channelAware(ChannelAware aware) {
        options.setChannelAware(aware);
        return this;
    }

    public ServerBuilder ioProcessors(int count) {
        options.setIoThreads(count);
        return this;
    }

    public ServerBuilder timeoutHander(TimeoutHandler handler) {
        options.setTimeoutHandler(handler);
        return this;
    }

    public SimpleTCPServer build() {
        return new SimpleTCPServer(ip, port, options);
    }
}
