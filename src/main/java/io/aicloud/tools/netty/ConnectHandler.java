package io.aicloud.tools.netty;

/**
 * @author fagongzi
 */
public interface ConnectHandler {
    /**
     * connect failed
     *
     * @param ip   target ip
     * @param port target port
     */
    void onFailed(String ip, int port);

    /**
     * reconnected
     *
     * @param ip   target ip
     * @param port target port
     */
    void onReconnected(String ip, int port);
}
