package io.aicloud.tools.netty;

/**
 * @author fagongzi
 */
public interface ConnectHandler {
    /**
     * connect failed
     *
     * @param connector connector
     * @param ip        target ip
     * @param port      target port
     */
    void onFailed(Connector connector, String ip, int port);

    /**
     * reconnected
     *
     * @param connector connector
     * @param ip        target ip
     * @param port      target port
     */
    void onReconnected(Connector connector, String ip, int port);
}
