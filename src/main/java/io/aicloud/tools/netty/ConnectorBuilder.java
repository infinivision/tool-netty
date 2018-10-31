package io.aicloud.tools.netty;

import io.netty.channel.EventLoopGroup;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Description:
 * <pre>
 * Date: 2018-10-25
 * Time: 10:23
 * </pre>
 *
 * @author fagongzi
 */
public class ConnectorBuilder<T> {
    private Server[] servers;
    private ConnectorOptions<T> options = new ConnectorOptions<>();

    public ConnectorBuilder(String... servers) {
        this.servers = new Server[servers.length];
        for (int i = 0; i < servers.length; i++) {
            this.servers[i] = new Server(servers[i]);
        }
    }

    public ConnectorBuilder<T> debug(boolean debug) {
        options.setDebug(debug);
        return this;
    }

    public ConnectorBuilder<T> socketSendAndRecvBuffer(int recv, int send) {
        options.setSocketReadBuffer(recv);
        options.setSocketWriteBuffer(send);
        return this;
    }

    public ConnectorBuilder<T> socketBacklog(int backlog) {
        options.setSocketBacklog(backlog);
        return this;
    }

    public ConnectorBuilder<T> socketTimeoutSeconds(int timeout) {
        options.setSocketTimeout(timeout);
        return this;
    }

    public ConnectorBuilder<T> maxPacketBodySize(int size) {
        options.setMaxBodySize(size);
        return this;
    }

    public ConnectorBuilder<T> allowReconnect(boolean allowReconnect, int heathCheckIntervalSecs) {
        options.setAllowReconnect(allowReconnect);
        options.setHeathCheckInterval(heathCheckIntervalSecs);
        return this;
    }

    public ConnectorBuilder<T> codec(Codec<T> codec) {
        options.setCodec(codec);
        return this;
    }

    public ConnectorBuilder<T> channelAware(ChannelAware<T> aware) {
        options.setChannelAware(aware);
        return this;
    }

    public ConnectorBuilder<T> eventGroup(EventLoopGroup group) {
        options.setGroup(group);
        return this;
    }

    public ConnectorBuilder<T> connectHandler(ConnectHandler handler) {
        options.setConnectHandler(handler);
        return this;
    }

    public ConnectorBuilder<T> executor(ScheduledExecutorService executor) {
        options.setExecutor(executor);
        return this;
    }

    public Connector<T> build() {
        return new Connector<>(options, servers);
    }
}
